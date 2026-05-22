package com.ingestion.client

import com.ingestion.common.ActorSystemBootstrap

object ClientApp:

  private val DefaultEndpointHost = "127.0.0.1"
  private val DefaultEndpointPort = 5150

  def main(args: Array[String]): Unit =
    val config       = ActorSystemBootstrap.loadConfig("application.conf")
    val (host, port) = parseArgs(args, config)
    val system       = ActorSystemBootstrap.start("ClientSystem", ClientBehavior(host, port), config)
    sys.addShutdownHook {
      system.terminate()
      ()
    }

  private def parseArgs(args: Array[String], config: com.typesafe.config.Config): (String, Int) =
    args.toList match
      case "--host" :: h :: "--port" :: p :: _ => (h, p.toInt)
      case _ =>
        (
          if config.hasPath("ingestion.endpoint.host") then config.getString("ingestion.endpoint.host")
          else DefaultEndpointHost,
          if config.hasPath("ingestion.endpoint.port") then config.getInt("ingestion.endpoint.port")
          else DefaultEndpointPort
        )
