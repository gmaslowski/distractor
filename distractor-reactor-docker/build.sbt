name := """distractor-reactor-docker"""

val logbackVersion = "1.1.3"
val akkaVersion = "2.4.11"

libraryDependencies ++= Seq(
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.github.docker-java" % "docker-java" % "3.0.6"
)
