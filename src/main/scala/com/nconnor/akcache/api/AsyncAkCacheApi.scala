package com.nconnor.akcache.api

import akka.Done
import akka.actor.ActorSystem
import javax.inject.Inject
import play.api.cache.AsyncCacheApi

import scala.concurrent.Future

/**
  * Author: Nicholas Connor
  * Date: 7/12/18
  * Package: com.nconnor.akcache.api.play
  */
class AsyncAkCacheApi @Inject() (system: ActorSystem) extends AkCache(system) with AsyncCacheApi {
  /**
    *
    * @return
    */
  override def removeAll(): Future[Done] = throw new UnsupportedOperationException
}