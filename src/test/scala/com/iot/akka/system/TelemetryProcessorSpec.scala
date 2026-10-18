package com.iot.akka.system

import akka.actor.ActorSystem
import akka.testkit.{TestKit, TestProbe}
import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class TelemetryProcessorSpec
  extends TestKit(ActorSystem("IoTDataSpec"))
  with AnyWordSpecLike
  with Matchers
  with BeforeAndAfterAll {

  override def afterAll(): Unit = {
    TestKit.shutdownActorSystem(system)
  }

  "TelemetryProcessor" should {
    "send alert when temperature exceeds 100" in {
      import TelemetryProcessor._

      val probe = TestProbe()
      val processor = system.actorOf(TelemetryProcessor.props(probe.ref))

      processor ! RawData("SENSOR_01", 104.5, System.currentTimeMillis())

      probe.expectMsgPF() {
        case Alert("SENSOR_01", temp, msg) =>
          temp shouldBe 104.5
          msg should include("CRITICAL_TEMPERATURE_ANOMALY")
      }
    }

    "not send alert for normal temperature" in {
      import TelemetryProcessor._

      val probe = TestProbe()
      val processor = system.actorOf(TelemetryProcessor.props(probe.ref))

      processor ! RawData("SENSOR_02", 80.0, System.currentTimeMillis())

      probe.expectNoMessage()
    }
  }
}
