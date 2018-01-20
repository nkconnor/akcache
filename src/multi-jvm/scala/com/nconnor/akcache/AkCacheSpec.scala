package com.nconnor.akcache

import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.remote.testconductor.RoleName
import akka.remote.testkit.{MultiNodeConfig, MultiNodeSpec, MultiNodeSpecCallbacks}
import org.scalatest._
import com.nconnor.akcache.api.AkCache
import com.typesafe.config.ConfigFactory

import scala.concurrent.duration.Duration

/**
 * Hooks up MultiNodeSpec with ScalaTest
 */
trait STMultiNodeSpec extends MultiNodeSpecCallbacks
    with FlatSpecLike with Matchers with BeforeAndAfterAll { self: MultiNodeSpec ⇒

  override def beforeAll() = multiNodeSpecBeforeAll()

  override def afterAll() = multiNodeSpecAfterAll()

}

/**
 *
 */
object MultiNodeSampleConfig extends MultiNodeConfig {
  val node1 = role("node1")
  val node2 = role("node2")

  commonConfig(ConfigFactory.parseString("""
    akka.cluster.metrics.enabled=off
    akka.actor.provider = "akka.cluster.ClusterActorRefProvider"
    """))
}

class AkCacheSpec extends MultiNodeSpec(MultiNodeSampleConfig) with STMultiNodeSpec {

  import MultiNodeSampleConfig._

  def initialParticipants = roles.size

  case class TestObj(name: String, attrs: Map[String, Any])


  lazy val testObj = TestObj("Henry", Map("teeth" -> "brown", "age" -> 58, "children" -> List("Susie", "Meg")))

  def join(from: RoleName, to: RoleName): Unit = {
    runOn(from) {
      Cluster(system) join node(to).address
      //val testCache = new AkCache(system)
    }
    enterBarrier(from.name + "-joined")
  }

  "nodes" should "join cluster" in {
    join(node1, node1)
    join(node2, node1)
    Thread.sleep(5000)
    assert(true)
  }

  "an akCache" should "set and get items" in {
    runOn(node1) {
      val testCache = new AkCache(system)


      val testSetIsGet = (kv: (String, Any)) => {
        testCache.set(kv._1, kv._2, Duration.Inf)
        assert(testCache.get(kv._1) == kv._2)
      }

      val test1 = ("an_int", 30)
      val test2 = ("henry", testObj)
      val test3 = ("some_list", List(1, 2, 3))
      val test4 = ("random_collection", Map(test1, test2, test3))

      testSetIsGet(test1)
      testSetIsGet(test2)
      testSetIsGet(test3)
      testSetIsGet(test4)
    }
  }
}