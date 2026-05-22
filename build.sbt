import Dependencies._

ThisBuild / organization := "com.ingestion"
ThisBuild / scalaVersion := "3.4.2"
ThisBuild / version      := "1.0.0-SNAPSHOT"
ThisBuild / scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-Xmax-inlines",
  "64"
)

lazy val root = (project in file("."))
  .aggregate(api, common, endpoint, client)
  .settings(
    name := "ingestion-pipeline",
    publish / skip := true
  )

lazy val api = (project in file("modules/ingestion-api"))
  .settings(
    name := "ingestion-api",
    libraryDependencies ++= circe
  )

lazy val common = (project in file("modules/ingestion-common"))
  .dependsOn(api)
  .settings(
    name := "ingestion-common",
    libraryDependencies ++= akkaTyped ++ typesafeConfig ++ logging
  )

lazy val endpoint = (project in file("modules/ingestion-endpoint"))
  .dependsOn(common)
  .settings(
    name := "ingestion-endpoint",
    libraryDependencies ++= akkaRemote,
    Compile / run / mainClass := Some("com.ingestion.endpoint.EndpointApp"),
    run / fork := true
  )

lazy val client = (project in file("modules/ingestion-client"))
  .dependsOn(common)
  .settings(
    name := "ingestion-client",
    libraryDependencies ++= akkaRemote,
    Compile / run / mainClass := Some("com.ingestion.client.ClientApp"),
    run / fork := true
  )

addCommandAlias("runEndpoint", "endpoint/run")
addCommandAlias("runClient", "client/run")
