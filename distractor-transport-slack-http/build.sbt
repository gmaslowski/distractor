val akkaVersion: String = "2.4.10"
val apacheHttpClientVersion: String = "4.5"

libraryDependencies ++= Seq(
  // Actor System
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,

  // Akka Http
  "com.typesafe.akka" %% "akka-http-core" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-experimental" % akkaVersion,
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % akkaVersion,

  // misc
  "commons-codec" % "commons-codec" % "1.10",
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % "2.8.2"
)