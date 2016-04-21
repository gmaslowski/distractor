import sbt._

lazy val distractor = project.in(file("."))
  .aggregate(distractor_core)

// core
lazy val distractor_api = Project(id = "distractor-api", base = file("distractor-api"))
lazy val distractor_core = Project(id = "distractor-core", base = file("distractor-core"))
  .dependsOn(distractor_api)

// ui
lazy val distractor_dashboard = Project(id = "distractor-dashboard", base = file("distractor-dashboard"))

// reactors
lazy val distractor_reactor_info = Project(id = "distractor-reactor-info", base = file("distractor-reactor-info"))
lazy val distractor_reactor_system = Project(id = "distractor-reactor-system", base = file("distractor-reactor-system"))
  .dependsOn(distractor_api)

// transport
lazy val distractor_transport_telnet = Project(id = "distractor-transport-telnet", base = file("distractor-transport-telnet"))
lazy val distractor_transport_http_rest = Project(id = "distractor-transport-http-rest", base = file("distractor-transport-http-rest"))
