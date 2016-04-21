name := """distractor-reactor-system"""
version := "0.1-SNAPSHOT"
scalaVersion := "2.11.8"

val akkaVersion = "2.4.3"

libraryDependencies ++= Seq(
  // Actor System
  "com.typesafe.akka" %% "akka-actor" % akkaVersion
)