
lazy val root = (project in file("."))
  .enablePlugins(CodeGenPlugin)
  .settings(
    scalaVersion := "2.13.8",
    version := "0.1"
  )