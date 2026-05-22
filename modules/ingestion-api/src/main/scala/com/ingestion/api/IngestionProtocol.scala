package com.ingestion.api

import io.circe.{Decoder, Encoder}
import io.circe.generic.semiauto.*

/** Shared contracts between ingestion clients and the endpoint service. */
object IngestionProtocol:

  final case class TestEvent(data: String)
  object TestEvent:
    given Encoder[TestEvent] = deriveEncoder
    given Decoder[TestEvent] = deriveDecoder

  final case class EventProcessed(date: Long)
  object EventProcessed:
    given Encoder[EventProcessed] = deriveEncoder
    given Decoder[EventProcessed] = deriveDecoder

  /** Well-known actor path segment for the remote ingestion handler. */
  val IngestionActorName = "ingestion"

  def ingestionActorPath(host: String, port: Int, systemName: String = "IngestionSystem"): String =
    s"akka://$systemName@$host:$port/user/${IngestionActorName}"
