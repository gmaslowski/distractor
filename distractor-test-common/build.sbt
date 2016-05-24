name := """distractor-test-common"""
version := "0.1-SNAPSHOT"
scalaVersion := "2.11.8"

val akkaVersion = "2.4.3"
val scalatestVersion = "2.2.6"
val mockitoVersion = "1.10.19"
val logbackVersion = "1.1.3"

libraryDependencies ++= Seq(
  // Actor System
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,

  // Testing Frameworks
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % scalatestVersion,
  "org.mockito" % "mockito-core" % mockitoVersion
)