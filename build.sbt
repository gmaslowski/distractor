name := """distractor"""

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.6"

val akkaVersion = "2.3.12"
val scalatestVersion = "2.2.5"
val scalamockVersion = "3.2.2"
val mockitoVersion = "1.10.19"

libraryDependencies ++= Seq(
  // Actor System
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  // Testing Frameworks
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % "test",
  "org.scalatest" %% "scalatest" % scalatestVersion % "test",
  "org.mockito" % "mockito-core" % mockitoVersion % "test"
)

resolvers += "Sonatype OSS Releases" at "https://oss.sonatype.org/content/repositories/releases/"
