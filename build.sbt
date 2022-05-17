import Dependencies._
import Settings.{fqClassNameFrom, sbtSettings, ProjectSyntax}

sbtSettings

lazy val `notifications-engine` =
  project
    .root("notifications-engine")
    .aggregate(
      `kafka-clients`,
      models,
      `models-avro`,
      `models-ciris`,
      `models-json`,
      `notifications-dispatcher`,
      `notifications-gateway`,
    )

lazy val `notifications-dispatcher` =
  project
    .application("notifications-dispatcher")
    .dependsOn(
      `kafka-clients` % "test->test;compile->compile",
      models % "test->test;compile->compile",
      `models-avro`,
      `models-ciris`,
      `models-json`,
    )
    .mainDependencies(
      catsCore,
      catsEffect,
      catsEffectKernel,
      catsEffectStd,
      ciris,
      fs2Core,
      fs2kafka,
      ip4sCore,
      log4catsCore,
      log4catsSlf4j,
    )
    .runtimeDependencies(log4jApi, log4jCore, log4jSlf4jImpl)
    .testDependencies(
      munit,
      munitCatsEffect,
      munitScalacheck,
      scalacheckEffect,
      scalacheckEffectMunit,
    )
    .settings(Compile / mainClass := fqClassNameFrom("NotificationsDispatcherApp"))

lazy val `notifications-gateway` =
  project
    .application("notifications-gateway")
    .dependsOn(
      `kafka-clients` % "test->test;compile->compile",
      models % "test->test;compile->compile",
      `models-avro`,
      `models-ciris`,
      `models-json`,
    )
    .mainDependencies(
      caseInsensitive,
      catsCore,
      catsEffect,
      catsEffectKernel,
      circeCore,
      ciris,
      fs2Core,
      fs2kafka,
      http4sCirce,
      http4sCore,
      http4sDsl,
      http4sEmberServer,
      http4sServer,
      ip4sCore,
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

lazy val `kafka-clients` = project
  .library("kafka-clients")
  .dependsOn(models % "test->test;compile->compile", `models-avro`)
  .mainDependencies(
    avro,
    catsCore,
    catsEffect,
    catsEffectKernel,
    fs2kafka,
    fs2kafkaVulcan,
    schemaRegistryClient,
    vulcan,
  )
  .testDependencies(
    fs2kafkaVulcanTestkitMunit,
    log4jApi,
    log4jCore,
    log4jSlf4jImpl,
    munit,
    munitCatsEffect,
    munitScalacheck,
    scalacheckEffect,
    scalacheckEffectMunit,
  )

lazy val models =
  project.library("models").mainDependencies(catsCore, ip4sCore).testDependencies(munit, scalacheck)

lazy val `models-avro` = project
  .library("models-avro")
  .dependsOn(models % "test->test;compile->compile")
  .mainDependencies(avro, catsCore, catsFree, catsKernel, ip4sCore, vulcan)
  .testDependencies(log4jApi, log4jCore, log4jSlf4jImpl, munit, munitScalacheck)

lazy val `models-ciris` = project
  .library("models-ciris")
  .dependsOn(models % "test->test;compile->compile")
  .mainDependencies(catsCore, ciris)

lazy val `models-json` = project
  .library("models-json")
  .dependsOn(models % "test->test;compile->compile")
  .mainDependencies(catsCore, circeCore, circeGeneric, ip4sCore)
  .testDependencies(circeParser, munit)
