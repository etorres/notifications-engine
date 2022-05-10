import com.typesafe.sbt.SbtNativePackager.Universal
import com.typesafe.sbt.packager.Keys.maintainer
import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import sbt.Keys._
import sbt.nio.Keys.{onChangedBuildSource, ReloadOnSourceChanges}
import sbt.{
  addCommandAlias,
  file,
  Compile,
  Def,
  Global,
  ModuleID,
  Project,
  Provided,
  Runtime,
  Test,
  ThisBuild,
}
import sbtide.Keys.idePackagePrefix
import scalafix.sbt.ScalafixPlugin.autoImport.scalafixSemanticdb
import wartremover.WartRemover.autoImport.{wartremoverErrors, Wart, Warts}

object Settings {
  def sbtSettings: Seq[Def.Setting[_]] = addCommandAlias(
    "check",
    "; undeclaredCompileDependenciesTest; unusedCompileDependenciesTest; scalafixAll; scalafmtSbtCheck; scalafmtCheckAll",
  )

  def welcomeMessage: Def.Setting[String] = onLoadMessage := {
    s"""Custom tasks:
       |check - run all project checks
       |""".stripMargin
  }

  private[this] val warts: Seq[wartremover.Wart] = Warts.unsafe.filter(_ != Wart.DefaultArguments)

  private[this] def commonSettings(projectName: String): Def.SettingsDefinition = Seq(
    name := projectName,
    ThisBuild / organization := "es.eriktorr",
    ThisBuild / version := "1.0.0",
    ThisBuild / idePackagePrefix := Some("es.eriktorr.notification_engine"),
    Global / excludeLintKeys += idePackagePrefix,
    ThisBuild / scalaVersion := "3.1.2",
    Global / cancelable := true,
    Global / fork := true,
    Global / onChangedBuildSource := ReloadOnSourceChanges,
    Compile / compile / wartremoverErrors ++= warts,
    Test / compile / wartremoverErrors ++= warts,
    ThisBuild / semanticdbEnabled := true,
    ThisBuild / semanticdbVersion := scalafixSemanticdb.revision,
    scalacOptions ++= Seq(
      "-Xfatal-warnings",
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Yexplicit-nulls", // https://docs.scala-lang.org/scala3/reference/other-new-features/explicit-nulls.html
      "-Ysafe-init", // https://docs.scala-lang.org/scala3/reference/other-new-features/safe-initialization.html
    ),
  )

  implicit class ProjectSyntax(project: Project) {
    def root(rootName: String): Project =
      project.in(file(".")).settings(Seq(name := rootName, publish / skip := true, welcomeMessage))

    private[this] def module(path: String): Project =
      project.in(file(path)).settings(commonSettings(project.id))

    def application(path: String): Project =
      module(s"apps/$path")
        .settings(Seq(Universal / maintainer := "https://eriktorr.es"))
        .enablePlugins(JavaAppPackaging)

    def library(path: String): Project = module("libs/" ++ path)

    private[this] def dependencies_(dependencies: Seq[ModuleID]): Project =
      project.settings(libraryDependencies ++= dependencies)

    def mainDependencies(dependencies: ModuleID*): Project = dependencies_(dependencies)
    def testDependencies(dependencies: ModuleID*): Project =
      dependencies_(dependencies.map(_ % Test))
    def providedDependencies(dependencies: ModuleID*): Project =
      dependencies_(dependencies.map(_ % Provided))
    def runtimeDependencies(dependencies: ModuleID*): Project =
      dependencies_(dependencies.map(_ % Runtime))
  }
}
