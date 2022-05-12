import sbt._

trait Avro {
  private[this] val organization = "org.apache.avro"
  private[this] val version = "1.11.0"

  val avro = organization % "avro" % "1.11.0"
}

trait Cats {
  private[this] val organization = "org.typelevel"

  private[this] val catsVersion = "2.7.0"
  private[this] val catsEffectVersion = "3.3.11"
  private[this] val kittensVersion = "3.0.0-M4"

  val catsCore = organization %% "cats-core" % catsVersion
  val catsEffect = organization %% "cats-effect" % catsEffectVersion
  val catsEffectKernel = organization %% "cats-effect-kernel" % catsEffectVersion
  val catsEffectStd = "org.typelevel" %% "cats-effect-std" % catsEffectVersion
  val catsFree = organization %% "cats-free" % catsVersion
  val catsKernel = organization %% "cats-kernel" % catsVersion
  val kittens = organization %% "kittens" % kittensVersion
}

trait Circe {
  private[this] val organization = "io.circe"
  private[this] val version = "0.14.1"

  val circeCore = organization %% "circe-core" % version
  val circeGeneric = organization %% "circe-generic" % version
  val circeParser = organization %% "circe-parser" % version
}

trait Ciris {
  private[this] val organization = "is.cir"
  private[this] val version = "2.3.2"

  val ciris = organization %% "ciris" % version
}

trait Fs2 {
  private[this] val organization = "co.fs2"
  private[this] val version = "3.2.7"

  val fs2Core = organization %% "fs2-core" % version
  val fs2Io = organization %% "fs2-io" % version
}

trait Fs2kafka {
  private[this] val organization = "com.github.fd4s"
  private[this] val version = "2.4.0"

  val fs2kafka = organization %% "fs2-kafka" % version
  val fs2kafkaVulcan = organization %% "fs2-kafka-vulcan" % version
}

trait Http4s {
  private[this] val organization = "org.http4s"
  private[this] val version = "0.23.11"

  val http4sCirce = organization %% "http4s-circe" % version
  val http4sCore = organization %% "http4s-core" % version
  val http4sDsl = organization %% "http4s-dsl" % version
  val http4sEmberServer = organization %% "http4s-ember-server" % version
}

trait Ip4s {
  private[this] val organization = "com.comcast"
  private[this] val version = "3.1.2"

  val ip4sCore = organization %% "ip4s-core" % version
}

trait Log4cats {
  private[this] val organization = "org.typelevel"
  private[this] val version = "2.3.1"

  val log4catsCore = organization %% "log4cats-core_sjs1" % version
  val log4catsSlf4j = organization %% "log4cats-slf4j" % version
}

trait Log4j {
  private[this] val organization = "org.apache.logging.log4j"
  private[this] val version = "2.17.2"

  val log4jApi = organization % "log4j-api" % version
  val log4jCore = organization % "log4j-core" % version
  val log4jSlf4jImpl = organization % "log4j-slf4j-impl" % version
}

trait Munit {
  private[this] val scalametaOrg = "org.scalameta"
  private[this] val scalametaVersion = "0.7.29"

  private[this] val typelevelOrg = "org.typelevel"
  private[this] val scalacheckEffectVersion = "1.0.4"

  val munit = scalametaOrg %% "munit" % scalametaVersion
  val munitScalacheck = scalametaOrg %% "munit-scalacheck" % scalametaVersion
  val munitCatsEffect = typelevelOrg %% "munit-cats-effect-3" % "1.0.7"
  val scalacheckEffect = typelevelOrg %% "scalacheck-effect" % scalacheckEffectVersion
  val scalacheckEffectMunit = typelevelOrg %% "scalacheck-effect-munit" % scalacheckEffectVersion
}

trait Scalacheck {
  private[this] val organization = "org.scalacheck"
  private[this] val version = "1.16.0"

  val scalacheck = organization %% "scalacheck" % version
}

trait Vulcan {
  private[this] val organization = "com.github.fd4s"
  private[this] val version = "1.8.3"

  val vulcan = organization %% "vulcan" % version
}

object Dependencies
    extends Avro
    with Cats
    with Circe
    with Ciris
    with Fs2
    with Fs2kafka
    with Http4s
    with Ip4s
    with Log4cats
    with Log4j
    with Munit
    with Scalacheck
    with Vulcan
