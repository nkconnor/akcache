package com.nconnor.akcache.api

import akka.actor.ActorSystem
import javax.inject.Inject
import play.api.cache.SyncCacheApi

import scala.concurrent.duration.Duration
import scala.reflect.ClassTag

/**
  * Author: Nicholas Connor
  * Date: 7/12/18
  * Package: com.nconnor.akcache.api.play
  */
class SyncAkCacheApi @Inject() (val system: ActorSystem)
  extends AkCache with AkCluster with SyncCacheApi {

  import com.nconnor.akcache.core._

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
    super.akGetOrElse(key, expiration)(orElse)


  /**
    * Set a value into the cache.
    *
    * @param key Item key.
    * @param value Item value.
    * @param expiration Expiration time.
    */
  def set(key: String, value: Any, expiration: Duration = Duration.Inf): Unit =
    super.akSet(key, value, expiration)

  /**
    * Remove a value from the cache
    */
  def remove(key: String): Unit = super.akRemove(key)

  /**
    * Retrieve a value from the cache for the given type
    *
    * @param key Item key.
    * @return result as Option[T]
    */
  override def get[T: ClassTag](key: String): Option[T] = futureOpt2T[T](super.akGet[T](key))

}
