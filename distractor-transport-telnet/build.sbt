name := """distractor-transport-telnet"""
version := "0.1-SNAPSHOT"
scalaVersion := "2.11.8"

val akkaVersion = "2.4.3"
val minaVersion = "2.0.9"

libraryDependencies ++= Seq(
  // Actor System
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,

  // Apache Mina
  "org.apache.mina" % "mina-core" % minaVersion
)