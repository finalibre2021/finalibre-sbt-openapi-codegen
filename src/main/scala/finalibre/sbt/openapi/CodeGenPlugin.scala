package finalibre.sbt.openapi

import sbt.*
import sbt.plugins.JvmPlugin
import sbt.Keys.{fullClasspath, javaHome, javaOptions, libraryDependencies}

object CodeGenPlugin extends AutoPlugin {

  override def trigger = allRequirements
  override def requires =JvmPlugin



  object autoImport {


    val openApiAuthorization : SettingKey[Option[String]] = settingKey[Option[String]](
      "Authorization header when fetching Open API definitions of form: name:header with header as a comma-separated list of values"
    )

    val openApiAdditionalProperties : SettingKey[Option[String]] = settingKey[Option[String]](
      "Addition properties for Open API generation"
    )

    val openApiPackage : SettingKey[Option[String]] = settingKey[Option[String]](
      "Package name for generated client"
    )

    val openApiArtifactVersion : SettingKey[Option[String]] = settingKey[Option[String]](
      "Version # for generated client"
    )

    val openApiConfigFile : SettingKey[Option[sbt.File]] = settingKey[Option[File]](
      "Optional config file for specifying properties of generation"
    )

    val openApiGitRepoId : SettingKey[Option[String]] = settingKey[Option[String]](
      "ID of Git project"
    )
    val openApiGitUserId : SettingKey[Option[String]] = settingKey[Option[String]](
      "User ID for Git connection"
    )
    val openApiGroupId : SettingKey[Option[String]] = settingKey[Option[String]](
      "Group ID"
    )
    val openApiHttpUserAgent : SettingKey[Option[String]] = settingKey[Option[String]](
      "User agent for open API generation"
    )
    val openApiIgnoreFileOverwrite : SettingKey[Boolean] = settingKey[Boolean](
      "Ignore file overwrites during open API generation?"
    )


    val openApiImportMappings : SettingKey[Option[String]] = settingKey[Option[String]](

      "Import mappings for Open API generation"
    )
    val openApiInstantiationTypes : SettingKey[Option[String]] = settingKey[Option[String]](
      "Instantiation types for Open API generation"
    )
    val openApiInvokerPackage : SettingKey[Option[String]] = settingKey[Option[String]](
      "Package for generated Open API invoker"
    )
    val openApiLanguageSpecificPrimitives : SettingKey[Option[String]] = settingKey[Option[String]](
      "Language specific primitives for Open API generation"
    )
    val openApiLibrary : SettingKey[Option[String]] = settingKey[Option[String]](
      "Library for Open API generation"
    )
    val openApiModelNamePrefix : SettingKey[Option[String]] = settingKey[Option[String]](
      "Prefix name used for generated Open API model classes"
    )
    val openApiModelNameSuffix : SettingKey[Option[String]] = settingKey[Option[String]](
      "Suffix name used for generated Open API model classes"
    )
    val openApiModelPackage : SettingKey[Option[String]] = settingKey[Option[String]](
      "Package name used for generated Open API model classes"
    )

    val openApiReleaseNote : SettingKey[Option[String]] = settingKey[Option[String]](
      "Release note for generated Open API client"
    )
    val openApiRemoveOperationIdPrefix : SettingKey[Option[String]] = settingKey[Option[String]](
      "Remove operation id prefix"
    )
    val openApiReservedNameMappings : SettingKey[Option[String]] = settingKey[Option[String]](
      "Reserved name mappings for generated Open API client"
    )
    val openApiSkipOverwrite : SettingKey[Boolean] = settingKey[Boolean](
      "Skip overwrite during open API generation?"
    )

    val openApiTemplatesDirectory : SettingKey[Option[sbt.File]] = settingKey[Option[File]](
      "Template directory used for generated Open API client"
    )
    val openApiTypeMappings : SettingKey[Option[String]] = settingKey[Option[String]](
      "Type mappings used for generated Open API client"
    )
    val openApiVerbose : SettingKey[Boolean] = settingKey[Boolean](
      "Verbose during Open API generation"
    )



    val openApiSpecification : SettingKey[sbt.File] = settingKey[java.io.File](
      "The file containing the swagger json file to use for generating client"
    )
    val openApiClientLanguage : SettingKey[String] = settingKey[String](
      "The language (and flavor) for the generated client"
    )
    val openApiClientOutputFolder : SettingKey[Option[sbt.File]] = settingKey[Option[java.io.File]](
      "Optional output folder. If not specified, the default managed source folder is used"
    )
    val openApiGenerateClient : TaskKey[Seq[sbt.File]] = TaskKey[Seq[java.io.File]](
      "openapi-generate-client",
      "Calls the swagger codegen cli library to generate OpenAPI client"
    )
    val openApiBuildJavaInstallation : SettingKey[Option[sbt.File]] = SettingKey[Option[File]](
      "openapi-build-java-installation",
      "The java.exe (or javaw.exe) to use for executing the swagger codegen CLI. If left blank, " +
        "this plugin expects the java installation to be resolved through the PATH variable"
    )

  }

  import autoImport._

  override lazy val globalSettings = Seq(
    openApiClientOutputFolder := None,
    openApiAuthorization := None,
    openApiAdditionalProperties := None,
    openApiPackage := None,
    openApiArtifactVersion := None,
    openApiConfigFile := None,
    openApiGitRepoId := None,
    openApiGitUserId := None,
    openApiGroupId := None,
    openApiHttpUserAgent := None,
    openApiIgnoreFileOverwrite := false,
    openApiImportMappings := None,
    openApiInstantiationTypes := None,
    openApiInvokerPackage := None,
    openApiLanguageSpecificPrimitives := None,
    openApiModelNamePrefix := None,
    openApiModelNameSuffix := None,
    openApiModelPackage := None,
    openApiPackage := None,
    openApiReleaseNote := None,
    openApiRemoveOperationIdPrefix := None,
    openApiReservedNameMappings := None,
    openApiSkipOverwrite := false,
    openApiVerbose := false,
    openApiLibrary := None,
    openApiTemplatesDirectory := None,
    openApiTypeMappings := None,

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
      runSwaggerPlugin(
        swaggerFile,
        outDir,
        openApiClientLanguage.value,
        javaCommand ,
        cliCodegenJar.get.data.getAbsolutePath,
        openApiAuthorization.value,
        openApiAdditionalProperties.value,
        openApiPackage.value,
        openApiArtifactVersion.value,
        openApiConfigFile.value,
        openApiGitRepoId.value,
        openApiGitUserId.value,
        openApiGroupId.value,
        openApiHttpUserAgent.value,
        openApiIgnoreFileOverwrite.value,
        openApiImportMappings.value,
        openApiInstantiationTypes.value,
        openApiInvokerPackage.value,
        openApiLanguageSpecificPrimitives.value,
        openApiLibrary.value,
        openApiModelNamePrefix.value,
        openApiModelNameSuffix.value,
        openApiModelPackage.value,
        openApiReleaseNote.value,
        openApiRemoveOperationIdPrefix.value,
        openApiReservedNameMappings.value,
        openApiSkipOverwrite.value,
        openApiTemplatesDirectory.value,
        openApiTypeMappings.value,
        openApiVerbose.value

      )
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
     "--type-mappings" -> typeMappings
    ).collect {
      case (nam, Some(value)) => nam -> value
    }

    val execArgs = mandatoryArgs ++ optionalArgs

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
