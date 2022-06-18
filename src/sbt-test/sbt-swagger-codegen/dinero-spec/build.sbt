

lazy val root = (project in file("."))
  .enablePlugins(CodeGenPlugin)
  .settings(
    scalaVersion := "2.13.8",
    version := "0.1",
    swaggerClientLanguage := "scala",
    swaggerSpecification := file("swagger-dinero.json"),
      (Compile / swaggerBuildJavaInstallation) := Some(file("C:\\tools\\openjdk-17.0.2_windows-x64_bin\\bin\\java.exe"))

  )