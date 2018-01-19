package com.nconnor.akcache.core.models

import scala.concurrent.duration.Duration

/**
  * Author: Nicholas Connor
  * Date: 1/19/18
  * Package: core.models
  */
trait AkMessage {
  val key: String
}

trait AkResponse {
  val ITEM_EXPIRED = "item-expired"
}

case class AkSet(key: String, any: Any, expiration: Option[Duration]) extends AkMessage

case class AkRemove(key: String) extends AkMessage

case class AkGet(key: String, expiration: Option[Duration]) extends AkMessage

