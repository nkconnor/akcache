package com.nconnor.akcache.core.models

import scala.concurrent.duration.Duration

/**
 * Author: Nicholas Connor
 * Date: 1/19/18
 * Package: core.models
 *
 */
trait AkMessage {
  val key: String
}

/**
 *
 * @param key
 * @param any
 * @param expiration
 */
case class AkSet(key: String, any: Any, expiration: Option[Duration]) extends AkMessage

/**
 *
 * @param key
 */
case class AkRemove(key: String) extends AkMessage

/**
 *
 * @param key
 * @param expiration
 */
case class AkGet(key: String, expiration: Option[Duration]) extends AkMessage

