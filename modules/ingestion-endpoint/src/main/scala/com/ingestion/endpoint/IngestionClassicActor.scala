package com.ingestion.endpoint

import akka.actor.{Actor, ActorLogging}
import com.ingestion.api.IngestionProtocol.TestEvent
import com.ingestion.api.IngestionProtocol.EventProcessed
import io.circe.parser.decode
import io.circe.syntax.*

/** Classic actor handler — interoperates cleanly with remote classic/typed clients over Artery. */
final class IngestionClassicActor extends Actor with ActorLogging:

  override def receive: Receive =
    case event: String =>
      decode[TestEvent](event) match
        case Right(parsed) =>
          log.info("Processed event from {}: {}", sender(), parsed)
          val response = EventProcessed(System.currentTimeMillis()).asJson.noSpaces
          sender() ! response
        case Left(err) =>
          log.warning("Ignoring malformed payload: {} ({})", event, err.getMessage)

    case other =>
      log.warning("Ignoring unsupported message type: {}", other)
