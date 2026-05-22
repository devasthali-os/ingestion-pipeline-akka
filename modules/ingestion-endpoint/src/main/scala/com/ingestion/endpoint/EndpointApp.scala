package com.ingestion.endpoint

import akka.actor.Props
import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.scaladsl.adapter.given
import com.ingestion.api.IngestionProtocol.IngestionActorName
import com.ingestion.common.ActorSystemBootstrap

object EndpointApp:

  def main(args: Array[String]): Unit =
    val config = ActorSystemBootstrap.loadConfig("application.conf")
    val system = ActorSystemBootstrap.start(
      "IngestionSystem",
      Behaviors.setup { context =>
        context.actorOf(Props[IngestionClassicActor](), IngestionActorName)
        context.log.info(
          "Ingestion endpoint is ready on artery port {}",
          config.getString("akka.remote.artery.canonical.port")
        )
        Behaviors.empty
      },
      config
    )

    sys.addShutdownHook {
      system.terminate()
      ()
    }
