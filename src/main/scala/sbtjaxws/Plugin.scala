package sbtjaxws

import sbt._
import sbt.Keys._
import com.sun.tools.ws.WsImport

object Plugin extends sbt.Plugin {

  val SbtJaxWs = config("sbtjaxws")

  object SbtJaxWsKeys {

    val wsimport = TaskKey[Seq[File]]("jaxws-wsimport", "Generates Java code from WSDL")
    val wsdlFiles = SettingKey[Seq[File]]("jaxws-wsdl-files")
    val targetVersion = SettingKey[String]("jaxws-target-version", "Version of JAX-WS to target, default 2.2")
    val bindingFiles = SettingKey[Seq[File]]("jaxws-binding-files", "Files to use for bindings (-b)")
    val otherArgs = SettingKey[Seq[String]]("jaxws-other-args", "Other arguments to pass to wsimport")

  }

  import SbtJaxWsKeys._

  val sbtJaxWsSettings: Seq[Setting[_]] =
    Seq(
      javaSource in SbtJaxWs <<= sourceManaged in Compile,
      wsdlFiles := Nil,
      bindingFiles := Nil,
      targetVersion := "2.2",
      otherArgs := Nil,
      wsimport <<= (streams, wsdlFiles, javaSource in SbtJaxWs, bindingFiles, targetVersion, otherArgs) map { runWsImports },
      sourceGenerators in Compile <+= wsimport,
      managedSourceDirectories in Compile <+= (javaSource in SbtJaxWs),
      cleanFiles <+= (javaSource in SbtJaxWs))

  private case class WsImportSettings(dest: File, bindings: Seq[File], targetVersion: String, otherArgs: Seq[String])

  private def runWsImports(
    streams: TaskStreams,
    wsdlFiles: Seq[File],
    dest: File,
    bindings: Seq[File],
    targetVersion: String,
    otherArgs: Seq[String]): Seq[File] =
    wsdlFiles.flatMap(wsdl =>
      runWsImport(streams, wsdl, WsImportSettings(dest, bindings, targetVersion, otherArgs)))

  private def makeArgs(wsdlFile: File, settings: WsImportSettings): Seq[String] =
    Seq("-Xnocompile", "-quiet") ++
      settings.bindings.flatMap(binding => Seq("-b", binding.getAbsolutePath)) ++
      Seq("-s", settings.dest.getAbsolutePath) ++
      Seq("-target", settings.targetVersion) ++
      settings.otherArgs ++
      Seq(wsdlFile.getAbsolutePath)

  private def runWsImport(streams: TaskStreams, wsdlFile: File, settings: WsImportSettings): Seq[File] = {
    streams.log.info("Generating Java from " + wsdlFile)

    streams.log.debug("Creating dir " + settings.dest)
    settings.dest.mkdirs()

    val args = makeArgs(wsdlFile, settings)
    streams.log.debug("wsimport " + args.mkString(" "))
    try {
      val result = WsImport.doMain(args.toArray)
      if (result != 0)
        throw new RuntimeException("Problem running wsimport")
    } catch {
      case t =>
        streams.log.error("Problem running wsimport " + args.mkString(" "))
        throw t
    }
    (settings.dest ** "*.java").get
  }

}
