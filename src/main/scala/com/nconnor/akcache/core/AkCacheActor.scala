package com.nconnor.akcache.core

import akka.actor.Actor
import com.nconnor.akcache.core.models.{AkGet, AkItem, AkRemove, AkSet}

/**
  * Author: Nicholas Connor
  * Date: 1/19/18
  * Package: core
  */
case class AkCacheActor() extends Actor {
  /**
      https://doc.akka.io/docs/akka/current/cluster-sharding.html#passivation
      If the state of the entities are persistent you may stop entities that are not used to reduce memory consumption.
      This is done by ..
   */
  context.setReceiveTimeout(AkCacheConfig.akCacheActorTimeout)

  private var state: Option[AkItem[Any]] = None

  override def receive = {
    case AkSet(key, value, maybeDuration) => // handle set
    case AkGet(key, maybeDuration) => // handle get
    case AkRemove(key) => // handle remove
  }
}
