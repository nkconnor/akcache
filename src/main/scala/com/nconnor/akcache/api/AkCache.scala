package com.nconnor.akcache.api

import javax.inject.Inject

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.cluster.sharding.{ClusterSharding, ClusterShardingSettings, ShardRegion}
import akka.pattern.ask

import play.api.cache.CacheApi

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.reflect.ClassTag
import scala.concurrent.duration._

import com.nconnor.akcache.core.AkCacheActor
import com.nconnor.akcache.core.models._

/**
  * Author: Nicholas Connor
  * Date: 1/19/18
  * Package: api
  */

trait AkCluster {

  /**
    * numberOfShards ::
    */
  private val numberOfShards: Int = 1600

  protected val extractEntityId: ShardRegion.ExtractEntityId = {
    case akMessage: AkMessage => (akMessage.key.toString, akMessage)
  }


  protected val extractShardId: ShardRegion.ExtractShardId = {
    case akMessage: AkMessage ⇒ (math.abs(akMessage.key.hashCode) % numberOfShards).toString
    case ShardRegion.StartEntity(id) ⇒
      // StartEntity is used by remembering entities feature
      (math.abs(id.hashCode) % numberOfShards).toString
  }
}

@Singleton
class AkCache @Inject()(system: ActorSystem) extends CacheApi with AkCluster {

  val shardRegion: ActorRef = ClusterSharding(system).start(
    typeName = "AkCacheActor",
    entityProps = Props[AkCacheActor],
    settings = ClusterShardingSettings(system),
    extractEntityId = extractEntityId,
    extractShardId = extractShardId)

  override def set(key: String, value: Any, expiration: Duration): Unit = shardRegion ! AkSet(key, value, Some(expiration))

  override def remove(key: String): Unit = shardRegion ! AkRemove(key)

  override def getOrElse[A](key: String, expiration: Duration)(orElse: => A)(implicit evidence$1: ClassTag[A]): A = {
    val akItem = shardRegion.ask(AkGet(key, Some(expiration)))
      .mapTo[Option[AkItem[A]]]
      .map {
        case Some(akItem) => akItem.any
        case None =>
          val A = orElse
          shardRegion ! AkSet(key, A, Some(expiration))
          A
        // case _ =>
        //   Logger.error(s"Retrieved akItem from key $key with an unknown type")
        //   orElse
      }

    Await.result(akItem, atMost = 10.seconds)
  }

  override def get[T](key: String)(implicit evidence$2: ClassTag[T]): Option[T] = {
    val akItem = shardRegion.ask(AkGet(key, None))
      .mapTo[Option[AkItem[T]]]
      .map(_.map(_.any))

    Await.result(akItem, atMost = 10.seconds)
  }
}
