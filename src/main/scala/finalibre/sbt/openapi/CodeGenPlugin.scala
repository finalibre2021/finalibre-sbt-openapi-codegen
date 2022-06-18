package finalibre.sbt.openapi

import sbt.*
import sbt.plugins.JvmPlugin
import sbt.Keys.{fullClasspath, javaHome, javaOptions, libraryDependencies}

object CodeGenPlugin extends AutoPlugin {

  override def trigger = allRequirements
  override def requires =JvmPlugin

  object autoImport {

    val openApiSpecification : SettingKey[java.io.File] = settingKey[java.io.File](
      "The file containing the swagger json file to use for generating client"
    )
    val openApiClientLanguage : SettingKey[String] = settingKey[String](
      "The language (and flavor) for the generated client"
    )
    val openApiClientOutputFolder : SettingKey[Option[java.io.File]] = settingKey[Option[java.io.File]](
      "Optional output folder. If not specified, the default managed source folder is used"
    )
    val openApiGenerateClient : TaskKey[Seq[java.io.File]] = TaskKey[Seq[java.io.File]](
      "openapi-generate-client",
      "Calls the swagger codegen cli library to generate OpenAPI client"
    )
    val openApiBuildJavaInstallation : SettingKey[Option[File]] = SettingKey[Option[File]](
      "openapi-build-java-installation",
      "The java.exe (or javaw.exe) to use for executing the swagger codegen CLI. If left blank, " +
        "this plugin expects the java installation to be resolved through the PATH variable"
    )

  }

  import autoImport._

  override lazy val globalSettings = Seq(
    (Compile / openApiClientOutputFolder) := None,
    javaOptions ++= Seq("--add-exports=swagger-codegen-cli/=ALL_UNNAMED"),
      libraryDependencies +=   "io.swagger.codegen.v3" % "swagger-codegen-cli" % "3.0.34"
  )

  override lazy val projectSettings = Seq(
    Compile / openApiGenerateClient := {
      val outDir = (Compile / openApiClientOutputFolder).value match {
        case Some(file) => file
        case None => (Compile / Keys.managedSourceDirectories).value.head / "openapi"
      }
      (Compile / fullClasspath).value.foreach(fil => println(s"  ${fil.data.getAbsolutePath}"))
      val cliCodegenJar = (Compile / fullClasspath).value.find(_.data.getName.toLowerCase.matches("(?i)(swagger-codegen-cli.*jar)"))
      val swaggerFile = openApiSpecification.value
      val javaCommand = (Compile / javaHome).value.map(_.getAbsolutePath).getOrElse("java")
      runSwaggerPlugin(swaggerFile, outDir, openApiClientLanguage.value, javaCommand , cliCodegenJar.get.data.getAbsolutePath)
    }
  )


  def runSwaggerPlugin(
                        inputFile : java.io.File,
                        outputDir : java.io.File,
                        language : String,
                        javaCommand : String,
                        codegenJarPath : String,
                        authorization : Option[String],
                        additionalProperties : Option[String],
                        apiPackage : Option[String],
                        artifactVersion : Option[String],
                        codegenConfigFile : Option[File],
                        systemProperties : Option[String],
                        gitRepoId : Option[String],
                        gitUserId : Option[String],
                        groupId : Option[String],
                        httpUserAgent : Option[String],
                        ignoreFileOverwrite : Boolean,
                        importMappings : Option[String],
                        instantiationTypes : Option[String],
                        invokerPackage : Option[String],
                        languageSpecificPrimitives : Option[String],
                        library : Option[String],
                        modelNamePrefix : Option[String],
                        modelNameSuffix : Option[String],
                        modelPackage : Option[String],
                        releaseNote : Option[String],
                        removeOperationIdPrefix : Option[String],
                        reservedWordsMappings : Option[String],
                        skipOverwrite : Boolean,
                        templatesDirectory : Option[File],
                        typeMappings : Option[String],
                        verbose : Boolean,
                        ) : Seq[File] = {
    val mandatoryArgs = Array(
      "generate",
      "-l",
      language,
      "-i",
      inputFile.getPath,
      "-o",
      outputDir.getPath,
      "--ignore-file-override",
      ignoreFileOverwrite.toString,
      "-s",
      skipOverwrite.toString,
      "-v",
      verbose.toString
    )
    val optionalArgs = List(
      "-a" -> authorization,
      "--additional-properties" -> additionalProperties,
      "--api-package" -> apiPackage,
      "--artifact-version" -> artifactVersion,
      "-c" -> codegenConfigFile,
      "-D" -> systemProperties,
      "--git-repo-id" -> gitRepoId,
      "--git-user-id" -> gitUserId,
      "--group-id" -> groupId,
      "--http-user-agent" -> httpUserAgent,
      "--import-mappings" -> importMappings,
      "--instantiation-types" -> instantiationTypes,
      "--invoker-package" -> invokerPackage,
      "--language-specific-primitives" -> languageSpecificPrimitives,
      "--library" -> library,
      "--model-name-prefix" -> modelNamePrefix,
      "--model-name-suffix" -> modelNameSuffix,
      "--model-package" -> modelPackage,
      "--release-note <release note>" -> releaseNote,
      "--remove-operation-id-prefix" -> removeOperationIdPrefix,
      "--reserved-words-mappings" -> reservedWordsMappings,
      "-t" -> templatesDirectory.map(_.getAbsolutePath) ,
     "--type-mappings" -> typeMappings ]

    )

    if(outputDir.exists)
      outputDir.delete()
    val argString = execArgs.mkString(" ")

    import scala.sys.process._
    val result = s"${javaCommand} -jar ${codegenJarPath} $argString"!;
    println(s"Result from external project: $result")
    println(s"Scanning output directory: ${outputDir}")
    collectFiles(outputDir)
  }

  def collectFiles(dir : java.io.File, spaces : String = "") : Seq[java.io.File] = {
    println(dir)
    dir.listFiles()
      .filter(_ != null)
      .flatMap(f => if(f.isFile) List(f) else collectFiles(f, spaces + " "))

  }


}
