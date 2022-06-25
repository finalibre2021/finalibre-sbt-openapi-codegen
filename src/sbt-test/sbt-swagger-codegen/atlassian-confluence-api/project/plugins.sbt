
lazy val root = (project in file(".")).dependsOn(codeGenSbt)

lazy val codeGenSbt =
  new ProjectRef(file("../../../..").toURI, "root")
