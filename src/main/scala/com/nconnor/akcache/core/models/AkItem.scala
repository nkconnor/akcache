package com.nconnor.akcache.core.models

import scala.concurrent.duration.Duration

/**
  * Author: Nicholas Connor
  * Date: 1/19/18
  * Package: core.models
  */
case class AkItem[A](any: A, expiration: Duration)