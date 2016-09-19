import sbt.Keys._
import sbt._

lazy val akkaVersion = "2.4.10"

lazy val commonSettings = Seq(
  organization := "com.gmaslowski",
  version := "0.1-SNAPSHOT",
  scalaVersion := "2.11.8"
)

lazy val distractor = project.in(file("."))
  .settings(scalaVersion := "2.11.8")
  .aggregate(
    distractor_api,
    distractor_test_common,

    distractor_transport_telnet,
    distractor_transport_http_rest,
    distractor_transport_slack_http,

    distractor_reactor_system,
    distractor_reactor_jira,
    distractor_reactor_spring_boot_actuator,

    distractor_core
  ).dependsOn(distractor_core)

// commons
lazy val distractor_api = Project(id = "distractor-api", base = file("distractor-api"))
  .settings(libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion)
  .settings(commonSettings: _*)
lazy val distractor_test_common = Project(id = "distractor-test-common", base = file("distractor-test-common"))
  .settings(libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion)
  .settings(commonSettings: _*)

// core
lazy val distractor_core = Project(id = "distractor-core", base = file("distractor-core"))
  .settings(commonSettings: _*)
  .settings(libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion)
  .dependsOn(
    distractor_api,
    distractor_test_common,
    distractor_transport_telnet,
    distractor_transport_http_rest,
    distractor_transport_slack_http,
    distractor_reactor_system,
    distractor_reactor_jira,
    distractor_reactor_spring_boot_actuator)
  .settings(mainClass in(Compile, run) := Some("com.gmaslowski.distractor.core.DistractorBootstrap"))

// ui
lazy val distractor_dashboard = Project(id = "distractor-dashboard", base = file("distractor-dashboard"))
  .settings(commonSettings: _*)

// reactors
lazy val distractor_reactor_system = Project(id = "distractor-reactor-system", base = file("distractor-reactor-system"))
  .settings(libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion)
  .settings(commonSettings: _*)
  .dependsOn(distractor_api)
lazy val distractor_reactor_spring_boot_actuator = Project(id = "distractor-reactor-spring-boot-actuator", base = file("distractor-reactor-spring-boot-actuator"))
  .settings(libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion)
  .settings(commonSettings: _*)
  .dependsOn(distractor_api)
lazy val distractor_reactor_jira = Project(id = "distractor-reactor-jira", base = file("distractor-reactor-jira"))
  .settings(libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion)
  .settings(commonSettings: _*)
  .dependsOn(distractor_api)

// transports
lazy val distractor_transport_telnet = Project(id = "distractor-transport-telnet", base = file("distractor-transport-telnet"))
  .settings(libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion)
  .settings(commonSettings: _*)
  .dependsOn(distractor_api, distractor_test_common)
lazy val distractor_transport_http_rest = Project(id = "distractor-transport-http-rest", base = file("distractor-transport-http-rest"))
  .settings(libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion)
  .settings(commonSettings: _*)
  .dependsOn(distractor_api, distractor_test_common)
lazy val distractor_transport_slack_http = Project(id = "distractor-transport-slack-http", base = file("distractor-transport-slack-http"))
  .settings(libraryDependencies += "com.typesafe.akka" %% "akka-actor" % akkaVersion)
  .settings(commonSettings: _*)
  .dependsOn(distractor_api, distractor_test_common)

assemblyMergeStrategy in assembly := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case x => MergeStrategy.first
}

run in Compile <<= (run in Compile in distractor_core)