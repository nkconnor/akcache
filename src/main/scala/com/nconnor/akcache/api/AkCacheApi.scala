package com.nconnor.akcache.api

import akka.actor.ActorSystem
import javax.inject.Inject
import play.api.cache.CacheApi

import scala.concurrent.duration.Duration
import scala.reflect.ClassTag

import com.nconnor.akcache.core._

/**
  * Author: Nicholas Connor
  * Date: 7/12/18
  * Package: com.nconnor.akcache.api
  */
class AkCacheApi @Inject() (val system: ActorSystem)
  extends AkCache with AkCluster with CacheApi {
  override def set(key: String, value: Any, expiration: Duration): Unit = super.akSet(key, value, expiration)

  override def remove(key: String): Unit = super.akRemove(key)

  override def getOrElse[A](key: String, expiration: Duration)(orElse: => A)(implicit evidence$3: ClassTag[A]): A =
    super.akGetOrElse(key, expiration)(orElse)

  override def get[T](key: String)(implicit evidence$4: ClassTag[T]): Option[T] = futureOpt2T[T](super.akGet[T](key))
}