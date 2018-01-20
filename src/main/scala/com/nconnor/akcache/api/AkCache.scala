package com.nconnor.akcache.api

import javax.inject.Inject
import javax.inject.Singleton

import akka.actor.{ ActorRef, ActorSystem, Props }
import akka.cluster.sharding.{ ClusterSharding, ClusterShardingSettings, ShardRegion }
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

@Singleton
class AkCache @Inject() (system: ActorSystem) extends CacheApi with AkCluster {
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
  override def set(key: String, value: Any, expiration: Duration): Unit = shardRegion ! AkSet(key, value, Some(expiration))

  /**
   *
   * @param key
   */
  override def remove(key: String): Unit = shardRegion ! AkRemove(key)

  /**
   *
   * @param key
   * @param expiration
   * @param orElse
   * @param evidence$1
   * @tparam A
   * @return
   */
  override def getOrElse[A](key: String, expiration: Duration)(orElse: => A)(implicit evidence$1: ClassTag[A]): A = {
    get(key)
      .getOrElse {
        val A = orElse
        shardRegion ! AkSet(key, A, Some(expiration))
        A
      }
  }

  /**
   *
   * @param key
   * @param evidence$2
   * @tparam T
   * @return
   */
  override def get[T](key: String)(implicit evidence$2: ClassTag[T]): Option[T] = {
    val akItem = shardRegion.ask(AkGet(key, None))
      .mapTo[Option[AkItem[T]]]
      .map(_.map(_.any))

    Await.result(akItem, atMost = 10.seconds)
  }
}
