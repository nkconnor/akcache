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

import scala.util.{Failure, Success}

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

class AkCache(val system: ActorSystem) extends AkCluster {
  /**
    *
    */
  implicit val execIn = system.dispatcher

  /**
    *
    */
  implicit val getTimeout: akka.util.Timeout = 10.seconds

  /**
    *
    */
  private val shardRegion: ActorRef = ClusterSharding(system).start(
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
  def set(key: String, value: Any, expiration: Duration): Done = shardRegion ! AkSet(key, value, Some(expiration))

  /**
    *
    * @param key
    */
  def remove(key: String): Done = shardRegion ! AkRemove(key)

  /**
    *
    * @param key
    * @param expiration
    * @param orElse
    * @param evidence$1
    * @tparam A
    * @return
    */
  def getOrElse[A](key: String, expiration: Duration)(orElse: => Future[A])(implicit evidence$1: ClassTag[A]): Future[A] =
    get(key).flatMap {                               // why does the Async imply anything about obtaining A?
      _.getOrElse(orElse).andThen {
        case Success(item) => shardRegion ! AkSet(key, item, Some(expiration))
        case Failure(ex) => throw ex
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
  def getOrElse[A](key: String, expiration: Duration)(orElse: => A)(implicit evidence$1: ClassTag[A]): Future[A] =
    get(key).map {
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
  def get[T](key: String)(implicit evidence$2: ClassTag[T]): Future[Option[T]] =
    shardRegion.ask(AkGet(key, None))
      .mapTo[Option[AkItem[T]]]
      .map(_.map(_.any))
}
