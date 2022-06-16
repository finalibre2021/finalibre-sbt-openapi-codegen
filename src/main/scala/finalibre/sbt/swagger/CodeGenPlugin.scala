package finalibre.sbt.swagger

import sbt._
import sbt.plugins.JvmPlugin
import _root_.io.swagger.codegen.v3.cli.SwaggerCodegen

object CodeGenPlugin extends AutoPlugin {

  override def trigger = allRequirements
  override def requires =JvmPlugin

  object autoImport {

    val swaggerSpecification : SettingKey[java.io.File] = settingKey[java.io.File](
      "The file containing the swagger json file to use for generating client"
    )
    val language : SettingKey[String] = settingKey[String](
      "The language (and flavor) for the generated client"
    )

    val outputFolder : SettingKey[Option[java.io.File]] = settingKey[Option[java.io.File]](
      "Optional output folder. If not specified, the default managed source folder is used"
    )


    val generateSwaggerClient : TaskKey[Seq[java.io.File]] = TaskKey[Seq[java.io.File]](
      "generate-swagger-client",
      "Calls the swagger codegen cli library to generate OpenAPI client"
    )

  }

  import autoImport._

  override lazy val projectSettings = Seq(
    Compile / generateSwaggerClient := {
      val outDir = outputFolder.value match {
        case Some(file) => file
        case None => (Compile / Keys.managedSourceDirectories).value.head / "openapi"
      }
      runSwaggerPlugin(swaggerSpecification.value, outDir, language.value)
    }
  )


  def runSwaggerPlugin(inputFile : java.io.File, outputDir : java.io.File, language : String) : Seq[File] = {
    val execArgs = Array(
      "generate",
      "-l",
      language,
      "-i",
      inputFile.getPath,
      "-o",
      outputDir.getPath
    )
    var callResult = SwaggerCodegen.main(execArgs)
    collectFiles(outputDir)
  }

  def collectFiles(dir : java.io.File) : Seq[java.io.File] =
    dir.listFiles()
      .flatMap(f => if(f.isFile) List(f) else collectFiles(f))




}
