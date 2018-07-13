package com.nconnor.akcache.api

import akka.actor.ActorSystem
import javax.inject.Inject
import play.api.cache.CacheApi
import com.nconnor.akcache.core._

import scala.concurrent.duration.Duration
import scala.reflect.ClassTag

/**
  * Author: Nicholas Connor
  * Date: 7/12/18
  * Package: com.nconnor.akcache.api
  */
class AkCacheApi @Inject() (system: ActorSystem) extends AkCache(system) with CacheApi