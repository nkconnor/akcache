package com.nconnor.akcache.api

import akka.Done
import akka.actor.{ActorRef, ActorSystem, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}
import akka.pattern.ask

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.reflect.ClassTag
import scala.concurrent.duration._
import com.nconnor.akcache.core._
import com.nconnor.akcache.core.models._

/**
  * Author: Nicholas Connor
  * Date: 1/19/18
  * Package: api
  *
  */
trait AkCluster {
  /**
    * numberOfShards ::
    */
  private val numberOfShards: Int = 1600

  /**
    *
    */
  protected val extractEntityId: ShardRegion.ExtractEntityId = {
    case akMessage: AkMessage => (akMessage.key.toString, akMessage)
  }

  /**
    *
    */
  protected val extractShardId: ShardRegion.ExtractShardId = {
    case akMessage: AkMessage ⇒ (math.abs(akMessage.key.hashCode) % numberOfShards).toString
    case ShardRegion.StartEntity(id) ⇒
      // StartEntity is used by remembering entities feature
      (math.abs(id.hashCode) % numberOfShards).toString
  }
}

abstract class AkCache { self: AkCluster =>
  /**
    *
    */
  def system: ActorSystem
  /**
    *
    */
  implicit lazy val execIn = system.dispatcher

  /**
    *
    */
  implicit val getTimeout: akka.util.Timeout = 10.seconds

  /**
    *
    */
  private lazy val shardRegion: ActorRef = ClusterSharding(system).start(
    typeName = "AkCacheActor",
    entityProps = Props[AkCacheActor],
    settings = ClusterShardingSettings(system),
    extractEntityId = extractEntityId,
    extractShardId = extractShardId
  )

  /**
    *
    * @param key
    * @param value
    * @param expiration
    */
  def akSet(key: String, value: Any, expiration: Duration): Done = shardRegion ! AkSet(key, value, Some(expiration))

  /**
    *
    * @param key
    */
  def akRemove(key: String): Done = shardRegion ! AkRemove(key)

  /**
    *
    * @param key
    * @param expiration
    * @param orElse
    * @param evidence$1
    * @tparam A
    * @return
    */
  def akGetOrElseAsync[A](key: String, expiration: Duration)(orElse: => Future[A])(implicit evidence$1: ClassTag[A]): Future[A] =
    akGet[A](key).flatMap { // why does the Async imply anything about obtaining A?
      case Some(item) => Future.successful(item)
      case None => orElse map { item =>
        shardRegion ! AkSet(key, item, Some(expiration))
        item
      }
    }

  /**
    *
    * @param key
    * @param expiration
    * @param orElse
    * @param evidence$1
    * @tparam A
    * @return
    */
  def akGetOrElse[A](key: String, expiration: Duration)(orElse: => A)(implicit evidence$1: ClassTag[A]): Future[A] =
    akGet[A](key).map {
      _.getOrElse {
        val item = orElse
        shardRegion ! AkSet(key, item, Some(expiration))
        item
      }
    }

  /**
    *
    * @param key
    * @param evidence$2
    * @tparam T
    * @return
    */
  def akGet[T](key: String)(implicit evidence$2: ClassTag[T]): Future[Option[T]] =
    shardRegion.ask(AkGet(key, None))
      .mapTo[Option[AkItem[T]]]
      .map(_.map(_.any))
}
