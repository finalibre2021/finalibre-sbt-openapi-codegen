import _root_.finalibre.sbt.openapi

lazy val root = (project in file("."))
  .enablePlugins(CodeGenPlugin)
  .settings(
    scalaVersion := "2.13.8",
    version := "0.1",
    openApiClientLanguage := "scala",
    openApiSpecification := file("swagger-dinero.json"),
    openApiClientOutputFolder := Some(file("./generated")),
    scriptedBatchExecution := false,
      (Compile / openApiBuildJavaInstallation) := Some(file("C:\\tools\\openjdk-17.0.2_windows-x64_bin\\bin\\java.exe"))

  )