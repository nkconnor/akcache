package com.nconnor.akcache.api

import akka.actor.ActorSystem
import javax.inject.Inject
import play.api.cache.SyncCacheApi

import scala.concurrent.duration.Duration
import scala.reflect.ClassTag
import com.nconnor.akcache.core._

/**
  * Author: Nicholas Connor
  * Date: 7/12/18
  * Package: com.nconnor.akcache.api.play
  */
class SyncAkCacheApi @Inject() (system: ActorSystem) extends AkCache(system) with SyncCacheApi {
  /**
    *
    * @param key
    * @param expiration
    * @param orElse
    * @param evidence$1
    * @tparam A
    * @return
    */
  override def getOrElseUpdate[A](key: String, expiration: Duration)(orElse: => A)
                                 (implicit evidence$1: ClassTag[A]): A =
    getOrElse(key, expiration)(orElse)

}
