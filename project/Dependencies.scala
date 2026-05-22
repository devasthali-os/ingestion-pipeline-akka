import sbt._

object Dependencies {
  val akkaVersion     = "2.8.8"
  val circeVersion    = "0.14.10"
  val configVersion   = "1.4.3"
  val logbackVersion  = "1.5.12"

  val akkaTyped = Seq(
    "com.typesafe.akka" %% "akka-actor"       % akkaVersion,
    "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
    "com.typesafe.akka" %% "akka-slf4j"       % akkaVersion
  )

  val akkaRemote = Seq(
    "com.typesafe.akka" %% "akka-remote" % akkaVersion
  )

  val circe = Seq(
    "io.circe" %% "circe-core"    % circeVersion,
    "io.circe" %% "circe-generic" % circeVersion,
    "io.circe" %% "circe-parser"  % circeVersion
  )

  val typesafeConfig = Seq(
    "com.typesafe" % "config" % configVersion
  )

  val logging = Seq(
    "ch.qos.logback" % "logback-classic" % logbackVersion
  )
}
