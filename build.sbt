name := """distractor"""

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.7"

val akkaVersion = "2.4.1"
val scalatestVersion = "2.2.6"
val mockitoVersion = "1.10.19"
val minaVersion = "2.0.9"
val logbackVersion = "1.1.3"

libraryDependencies ++= Seq(
  // Actor System
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  // Goodies
  "org.apache.mina" % "mina-core" % minaVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  // Testing Frameworks
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "org.scalatest" %% "scalatest" % scalatestVersion % "test",
  "org.mockito" % "mockito-core" % mockitoVersion % "test"
)

mainClass in Compile := Some("com.gmaslowski.distractor.core.DistractorBootstrap")

resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases/"
