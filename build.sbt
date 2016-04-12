lazy val root = project.in(file("."))
  .aggregate(distractor_core)

lazy val distractor_core = project.in(file("distractor-core"))

lazy val distractor_dashboard = project.in(file("distractor-dashboard"))

lazy val distractor_reactor_info = project.in(file("distractor-reactor-info"))
lazy val distractor_reactor_system = project.in(file("distractor-reactor-system"))

lazy val distractor_transport_telnet = project.in(file("distractor-transport-telnet"))
lazy val distractor_transport_http_rest = project.in(file("distractor-transport-http-rest"))
