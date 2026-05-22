package com.ingestion.client

import akka.actor.typed.scaladsl.{ActorContext, Behaviors}
import akka.actor.typed.scaladsl.adapter.given
import akka.actor.typed.{ActorRef, Behavior, PostStop}
import akka.util.Timeout
import com.ingestion.api.IngestionProtocol
import com.ingestion.api.IngestionProtocol.TestEvent
import io.circe.syntax.*

import scala.concurrent.duration.*

object ClientBehavior:

  private given Timeout = Timeout(5.seconds)

  sealed trait ClientMessage
  private final case class Connect(endpointHost: String, endpointPort: Int) extends ClientMessage
  private final case class SendTestEvent(remote: akka.actor.ActorRef)           extends ClientMessage
  final case class EndpointReply(body: String)                                  extends ClientMessage

  def apply(endpointHost: String, endpointPort: Int): Behavior[ClientMessage] =
    Behaviors.setup { context =>
      val replyTo = context.messageAdapter[String](EndpointReply.apply)
      context.self ! Connect(endpointHost, endpointPort)
      active(context, replyTo)
    }

  private def active(
      context: ActorContext[ClientMessage],
      replyTo: ActorRef[String]
  ): Behavior[ClientMessage] =
    Behaviors
      .receiveMessage[ClientMessage] {
        case Connect(host, port) =>
          val path = IngestionProtocol.ingestionActorPath(host, port)
          val selection = context.system.classicSystem.actorSelection(path)
          context.pipeToSelf(selection.resolveOne()) {
            case scala.util.Success(ref) => SendTestEvent(ref)
            case scala.util.Failure(ex)  =>
              context.log.error("Could not resolve ingestion endpoint at {}: {}", path, ex.getMessage)
              Connect(host, port)
          }
          Behaviors.same

        case SendTestEvent(remote) =>
          val payload = TestEvent("some data").asJson.noSpaces
          remote.tell(payload, replyTo.toClassic)
          context.log.info("Sent test event to {}", remote)
          Behaviors.same

        case EndpointReply(notification) =>
          context.log.info("Received notification from ingestion endpoint: {}", notification)
          Behaviors.same
      }
      .receiveSignal {
        case (_, PostStop) => Behaviors.stopped
      }
