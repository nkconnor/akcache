package com.nconnor.akcache

import akka.Done

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

/**
  * Author: Nicholas Connor
  * Date: 7/12/18
  * Package: com.nconnor.akcache.core
  */
package object core {
  /**
    *
    * @param unit
    * @return
    */
  implicit def unit2Done(unit: Unit): Done = Done

  /**
    *
    * @param future
    * @tparam T
    * @return
    */
  implicit def future2T[T](future: Future[T]): T = Await.result(future, 10.seconds)

  /**
    *
    * @param future
    * @tparam T
    * @return
    */
  implicit def futureOpt2T[T](future: Future[Option[T]]): Option[T] = Await.result(future, 10.seconds)
}
