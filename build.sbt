import sbt._

lazy val distractor = project.in(file("."))
  .aggregate(
    distractor_api,
    distractor_test_common,

    distractor_transport_telnet,

    distractor_reactor_system,
    distractor_reactor_info,

    distractor_core
  )

// commons
lazy val distractor_api = Project(id = "distractor-api", base = file("distractor-api"))
lazy val distractor_test_common = Project(id = "distractor-test-common", base = file("distractor-test-common"))

// core
lazy val distractor_core = Project(id = "distractor-core", base = file("distractor-core"))
  .dependsOn(distractor_api, distractor_test_common, distractor_transport_telnet)

// ui
lazy val distractor_dashboard = Project(id = "distractor-dashboard", base = file("distractor-dashboard"))

// reactors
lazy val distractor_reactor_info = Project(id = "distractor-reactor-info", base = file("distractor-reactor-info"))
lazy val distractor_reactor_system = Project(id = "distractor-reactor-system", base = file("distractor-reactor-system"))
  .dependsOn(distractor_api)

// transport
lazy val distractor_transport_telnet = Project(id = "distractor-transport-telnet", base = file("distractor-transport-telnet"))
  .dependsOn(distractor_api, distractor_test_common)
lazy val distractor_transport_http_rest = Project(id = "distractor-transport-http-rest", base = file("distractor-transport-http-rest"))
