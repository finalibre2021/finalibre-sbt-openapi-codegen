package finalibre.sbt.swagger

import sbt.*
import sbt.plugins.JvmPlugin
import _root_.io.swagger.codegen.v3.cli.SwaggerCodegen
import sbt.Keys.{fullClasspath, javaHome, javaOptions, libraryDependencies}

object CodeGenPlugin extends AutoPlugin {

  override def trigger = allRequirements
  override def requires =JvmPlugin

  object autoImport {

    val swaggerSpecification : SettingKey[java.io.File] = settingKey[java.io.File](
      "The file containing the swagger json file to use for generating client"
    )
    val swaggerClientLanguage : SettingKey[String] = settingKey[String](
      "The language (and flavor) for the generated client"
    )

    val swaggerClientOutputFolder : SettingKey[Option[java.io.File]] = settingKey[Option[java.io.File]](
      "Optional output folder. If not specified, the default managed source folder is used"
    )

    val generateSwaggerClient : TaskKey[Seq[java.io.File]] = TaskKey[Seq[java.io.File]](
      "generate-swagger-client",
      "Calls the swagger codegen cli library to generate OpenAPI client"
    )

    val swaggerBuildJavaInstallation : SettingKey[Option[File]] = SettingKey[Option[File]](
      "swagger-build-java-installation",
      "The java.exe (or javaw.exe) to use for executing the swagger codegen CLI. If left blank, " +
        "this plugin expects the java installation to be resolved through the PATH variable"
    )

  }

  import autoImport._

  override lazy val globalSettings = Seq(
    (Compile / swaggerClientOutputFolder) := None,
    javaOptions ++= Seq("--add-exports=swagger-codegen-cli/=ALL_UNNAMED"),
      libraryDependencies +=   "io.swagger.codegen.v3" % "swagger-codegen-cli" % "3.0.34"
  )

  override lazy val projectSettings = Seq(
    Compile / generateSwaggerClient := {
      val outDir = (Compile / swaggerClientOutputFolder).value match {
        case Some(file) => file
        case None => (Compile / Keys.managedSourceDirectories).value.head / "openapi"
      }
      (Compile / fullClasspath).value.foreach(fil => println(s"  ${fil.data.getAbsolutePath}"))
      val cliCodegenJar = (Compile / fullClasspath).value.find(_.data.getName.toLowerCase.matches("(?i)(swagger-codegen-cli.*jar)"))
      val swaggerFile = swaggerSpecification.value
      val javaCommand = (Compile / javaHome).value.map(_.getAbsolutePath).getOrElse("java")
      runSwaggerPlugin(swaggerFile, outDir, swaggerClientLanguage.value, javaCommand , cliCodegenJar.get.data.getAbsolutePath)
    }
  )


  def runSwaggerPlugin(inputFile : java.io.File, outputDir : java.io.File, language : String, javaCommand : String, codegenJarPath : String) : Seq[File] = {
    val execArgs = Array(
      "generate",
      "-l",
      language,
      "-i",
      inputFile.getPath,
      "-o",
      outputDir.getPath
    )
    if(outputDir.exists)
      outputDir.delete()
    val argString = execArgs.mkString(" ")

    import scala.sys.process._
    val result = s"${javaCommand} -jar ${codegenJarPath} $argString"!;
    println(s"Result from external project: $result")
    println(s"Scanning output directory: ${outputDir}")
    //var callResult = SwaggerCodegen.main(execArgs)
    collectFiles(outputDir)
  }

  def collectFiles(dir : java.io.File, spaces : String = "") : Seq[java.io.File] = {
    println(dir)
    dir.listFiles()
      .filter(_ != null)
      .flatMap(f => if(f.isFile) List(f) else collectFiles(f, spaces + " "))

  }


}
