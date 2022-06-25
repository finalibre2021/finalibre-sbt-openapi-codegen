# Finalibre - SBT OpenAPI code generator
This SBT plugin isn't much but a simple wrapper around the Swagger Codegen library (https://github.com/swagger-api/swagger-codegen).
This project was initially forked from Reactif's SBT OpenAPI Code generator project (https://github.com/reactific/sbt-openapi-codegen). 
I forked said library because execution failed when building with JDK 17, due to the JDK
module system. In order to understand what is going on, I decided to start over on a blank sheet,
with the bare minimum of dependencies. 

## Disclaimer
Setting keys have been added to reflect the parameters that the underlying Swagger Codegen Library accepts. 
In some cases, I have had to guess on the functionality of parameters, and I have only tested the plugin on
a very limited number of test cases. 
I have seen the underlying Swagger Codegen Library generate faulty clients for both
Scala and Java, on the OpenAPI specification for the Atlassian Confluence Cloud REST API.
All in all, what I am trying to say is, use at your own risk.

## Usage
Of the parameters defined below, only `openApiSpecification` and `openApiClientLanguage`
are mandatory. 
The Swagger Codegen library does not define a JDK 9+ module export for the contained
file oas3.yaml specification of the Swagger Codegen API, and execution therefore fails 
during this plugin's call to the main-method of the CLI package.
For this reason, we execute the Swagger Codegen API by spawning a new JVM in a separate process.
This requires a Java JDK to be available to the new process. If you have already
defined a persistent PATH environment to resolve the correct JDK, everything should
work out-the-box. Otherwise, it will be necessary to assign the folder containing the correct java.exe
to the parameter `openApiBuildJavaInstallation`.


### Parameters

* `openApiSpecification : sbt.File` - The file containing the swagger json file to use for generating client

* `openApiClientLanguage : String` - The language (and flavor) for the generated client

* `openApiBuildJavaInstallation : Option[sbt.File]` - The java.exe (or javaw.exe) to use for executing the swagger codegen CLI. If left blank,
  this plugin expects the java installation to be resolved through the PATH variable


* `openApiAuthorization : Option[String]` - Authorization header when fetching Open API definitions of form: name:header with header as a comma-separated list of values

* `openApiAdditionalProperties : Option[String]` - Addition properties for Open API generation

* `openApiPackage : Option[String]` - Package name for generated client

* `openApiArtifactVersion : Option[String]` - Version # for generated client

* `openApiConfigFile : Option[sbt.File]` - Optional config file for specifying properties of generation

* `openApiGitRepoId : Option[String]` - ID of Git project

* `openApiGitUserId : Option[String]` - User ID for Git connection
 
* `openApiGroupId : Option[String]` - Group ID

* `openApiHttpUserAgent : Option[String]` - User agent for open API generation

* `openApiIgnoreFileOverwrite : Option[Boolean]` - UIgnore file overwrites during open API generation?

* `openApiImportMappings : Option[String]` - Import mappings for Open API generation

* `openApiInstantiationTypes : Option[String]` - Instantiation types for Open API generation

* `openApiInvokerPackage : Option[String]` - Package for generated Open API invoker

* `openApiLanguageSpecificPrimitives : Option[String]` - Language specific primitives for Open API generation

* `openApiLibrary : Option[String]` - Library for Open API generation

* `openApiModelNamePrefix : Option[String]` - Prefix name used for generated Open API model classes

* `openApiModelNameSuffix : Option[String]` - Suffix name used for generated Open API model classes

* `openApiModelPackage : Option[String]` - Package name used for generated Open API model classes

* `openApiReleaseNote : Option[String]` - Release note for generated Open API client

* `openApiRemoveOperationIdPrefix : Option[String]` - Remove operation id prefix

* `openApiReservedNameMappings : Option[String]` - Reserved name mappings for generated Open API client

* `openApiSkipOverwrite : Option[Boolean]` - Skip overwrite during open API generation?

* `openApiTemplatesDirectory : Option[sbt.File]` - Template directory used for generated Open API client

* `openApiTypeMappings : Option[String]` - Type mappings used for generated Open API client

* `openApiVerbose : Option[Boolean]` - Verbose during Open API generation

* `openApiClientOutputFolder : Option[String]` - Optional output folder. If not specified, the default managed source folder is used


