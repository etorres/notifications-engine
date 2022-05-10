import Dependencies._
import Settings.{sbtSettings, ProjectSyntax}

sbtSettings

lazy val `notification-engine` = project.root("notification-engine")

lazy val models = project.library("models").mainDependencies(catsCore)

lazy val `notification-gateway` =
  project
    .application("notification-gateway")
    .dependsOn(models % "test->test;compile->compile")
    .mainDependencies(catsCore, fs2Core, fs2Io, log4catsCore, log4catsSlf4j)
    .runtimeDependencies(log4jApi, log4jCore, log4jSlf4jImpl)
    .testDependencies(
      munit,
      munitCatsEffect,
      munitScalacheck,
      scalacheckEffect,
      scalacheckEffectMunit,
    )
    .settings(Compile / mainClass := Some("es.eriktorr.notification_engine.NotificationGatewayApp"))
