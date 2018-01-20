package com.nconnor.akcache

import akka.actor.{ Actor, ActorSystem }
import akka.remote.testkit.{ MultiNodeConfig, MultiNodeSpec, MultiNodeSpecCallbacks }
import org.scalatest._
import com.nconnor.akcache.api.AkCache

import scala.concurrent.duration.Duration

/**
 * Hooks up MultiNodeSpec with ScalaTest
 */
trait STMultiNodeSpec extends MultiNodeSpecCallbacks
    with FlatSpecLike with Matchers with BeforeAndAfterAll { self: MultiNodeSpec ⇒

  override def beforeAll() = multiNodeSpecBeforeAll()

  override def afterAll() = multiNodeSpecAfterAll()

}

trait MultiNodeSample {
  class Ponger extends Actor {
    def receive = {
      case "ping" => sender() ! "pong"
    }
  }
}

class MultiNodeSampleSpecMultiJvmNode1 extends MultiNodeAkCacheSpec
class MultiNodeSampleSpecMultiJvmNode2 extends MultiNodeAkCacheSpec

/**
 *
 */
object MultiNodeSampleConfig extends MultiNodeConfig {
  val node1 = role("node1")
  val node2 = role("node2")
}

class MultiNodeAkCacheSpec extends MultiNodeSpec(MultiNodeSampleConfig) with STMultiNodeSpec {

  def initialParticipants = roles.size

  case class TestObj(name: String, attrs: Map[String, Any])

  val testCache = new AkCache(system)

  lazy val testObj = TestObj("Henry", Map("teeth" -> "brown", "age" -> 58, "children" -> List("Susie", "Meg")))

  "an akCache" should "set and get items" in {

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