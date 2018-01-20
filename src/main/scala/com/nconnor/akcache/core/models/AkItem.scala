package com.nconnor.akcache.core.models

/**
 * Author: Nicholas Connor
 * Date: 1/19/18
 * Package: core.models
 *
 * @param any
 * @param expireAt
 * @tparam A
 */
case class AkItem[A](any: A, expireAt: Option[Long])