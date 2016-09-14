name := """distractor-test-common"""

val scalatestVersion = "2.2.6"
val mockitoVersion = "1.10.19"
val logbackVersion = "1.1.3"
val akkaVersion = "2.4.10"

libraryDependencies ++= Seq(
  // Testing Frameworks
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % scalatestVersion,
  "org.mockito" % "mockito-core" % mockitoVersion
)