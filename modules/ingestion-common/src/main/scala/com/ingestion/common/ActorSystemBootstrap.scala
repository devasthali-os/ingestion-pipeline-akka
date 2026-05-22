package com.ingestion.common

import akka.actor.typed.{ActorSystem, Behavior}
import com.typesafe.config.{Config, ConfigFactory}

object ActorSystemBootstrap:

  /** Load HOCON from the classpath (works in jars and sbt run). */
  def loadConfig(resourceName: String = "application.conf"): Config =
    ConfigFactory.load(resourceName)

  def start[T](name: String, behavior: Behavior[T], config: Config): ActorSystem[T] =
    ActorSystem(behavior, name, config)
