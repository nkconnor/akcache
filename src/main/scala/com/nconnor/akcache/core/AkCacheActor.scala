package com.nconnor.akcache.core

import java.time.LocalDateTime

import akka.actor.Actor
import com.nconnor.akcache.core.models.{ AkGet, AkItem, AkRemove, AkSet }
import java.time.Instant

/**
 * Author: Nicholas Connor
 * Date: 1/19/18
 * Package: core
 *
 */
case class AkCacheActor() extends Actor {
  /**
   * https://doc.akka.io/docs/akka/current/cluster-sharding.html#passivation
   * If the state of the entities are persistent you may stop entities that are not used to reduce memory consumption.
   * This is done by ..
   */
  context.setReceiveTimeout(AkCacheConfig.akCacheActorTimeout)

  /**
   * state::
   */
  private var state: Option[AkItem[Any]] = None

  override def receive = {
    case AkSet(key, value, maybeDuration) => state = Some(
      AkItem(value, maybeDuration.map(_.toMillis + Instant.now.toEpochMilli))
    )

    case AkGet(key, maybeDuration) => sender() ! state.flatMap { item =>
      if (item.expireAt.exists { _ < Instant.now.toEpochMilli }) None else Some(item)
    }
    case AkRemove(key) => state = None
  }
}
