package com.iot.akka.system

import akka.actor.{Actor, ActorRef, Props}

object TelemetryProcessor {
  case class RawData(sensorId: String, temperature: Double, timestamp: Long)
  case class Alert(sensorId: String, temperature: Double, message: String)

  def props(aiEngine: ActorRef): Props =
    Props(new TelemetryProcessor(aiEngine))
}

class TelemetryProcessor(aiEngine: ActorRef) extends Actor {
  import TelemetryProcessor._

  val CriticalThreshold = 100.0

  def receive: Receive = {
    case RawData(id, temp, time) =>
      if (temp > CriticalThreshold) {
        aiEngine ! Alert(
          id, temp,
          s"CRITICAL_TEMPERATURE_ANOMALY DETECTED AT $time"
        )
      }
    case _ =>
  }
}
