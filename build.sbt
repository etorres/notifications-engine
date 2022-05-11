import Dependencies._
import Settings.{fqClassNameFrom, sbtSettings, ProjectSyntax}

sbtSettings

lazy val `notifications-engine` =
  project
    .root("notifications-engine")
    .aggregate(
      models,
      `models-avro`,
      `models-json`,
      `notifications-dispatcher`,
      `notifications-gateway`,
    )

lazy val `notifications-dispatcher` =
  project
    .application("notifications-dispatcher")
    .dependsOn(models % "test->test;compile->compile")
    .mainDependencies()
    .runtimeDependencies(log4jApi, log4jCore, log4jSlf4jImpl)
    .testDependencies()
    .settings(Compile / mainClass := fqClassNameFrom("NotificationsDispatcherApp"))

lazy val `notifications-gateway` =
  project
    .application("notifications-gateway")
    .dependsOn(models % "test->test;compile->compile")
    .mainDependencies(
      catsCore,
      fs2Core,
      fs2Io,
      fs2kafka,
      fs2kafkaVulcan,
      log4catsCore,
      log4catsSlf4j,
    )
    .runtimeDependencies(log4jApi, log4jCore, log4jSlf4jImpl)
    .testDependencies(
      fs2kafkaVulcanTestkitMunit,
      munit,
      munitCatsEffect,
      munitScalacheck,
      scalacheckEffect,
      scalacheckEffectMunit,
    )
    .settings(Compile / mainClass := fqClassNameFrom("NotificationsGatewayApp"))

lazy val models = project.library("models").mainDependencies(catsCore)

lazy val `models-avro` = project
  .library("models-avro")
  .dependsOn(models % "test->test;compile->compile")
  .mainDependencies(catsCore, vulcan)

lazy val `models-json` = project
  .library("models-json")
  .dependsOn(models % "test->test;compile->compile")
  .mainDependencies(catsCore)
