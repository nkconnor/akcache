package com.nconnor.akcache.api

import akka.Done
import akka.actor.ActorSystem
import javax.inject.Inject
import play.api.cache.AsyncCacheApi

import scala.concurrent.Future
import scala.concurrent.duration.Duration
import scala.reflect.ClassTag

/**
  * Author: Nicholas Connor
  * Date: 7/12/18
  * Package: com.nconnor.akcache.api.play
  */
class AsyncAkCacheApi @Inject() (val system: ActorSystem)
  extends AkCache with AkCluster with AsyncCacheApi {
  /**
    *
    * @return
    */
  override def removeAll(): Future[Done] = throw new UnsupportedOperationException

  /**
    *
    * @param key
    * @param expiration
    * @param orElse
    * @param evidence$1
    * @tparam A
    * @return
    */
  override def getOrElseUpdate[A](key: String, expiration: Duration)(orElse: => Future[A])
                                 (implicit evidence$1: ClassTag[A]): Future[A] =
    super.akGetOrElseAsync(key, expiration)(orElse)

  /**
    * Set a value into the cache.
    *
    * @param key Item key.
    * @param value Item value.
    * @param expiration Expiration time.
    */
  def set(key: String, value: Any, expiration: Duration = Duration.Inf): Future[Done] =
    Future.successful(super.akSet(key, value, expiration))

  /**
    * Remove a value from the cache
    */
  def remove(key: String): Future[Done] = Future.successful(super.akRemove(key))

  /**
    * Retrieve a value from the cache for the given type
    *
    * @param key Item key.
    * @return result as a future of Option[T]
    */
  def get[T: ClassTag](key: String): Future[Option[T]] = super.akGet(key)

}