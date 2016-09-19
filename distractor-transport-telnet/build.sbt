name := """distractor-transport-telnet"""

val minaVersion = "2.0.9"

libraryDependencies ++= Seq(
  "org.apache.mina" % "mina-core" % minaVersion,
  "com.typesafe.akka" %% "akka-http-spray-json-experimental" % "2.4.10"
)