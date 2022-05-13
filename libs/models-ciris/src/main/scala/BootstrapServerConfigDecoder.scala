package es.eriktorr.notifications_engine

import config.KafkaConfig.BootstrapServer

import ciris.{ConfigDecoder, ConfigError}

trait BootstrapServerConfigDecoder:
  implicit def bootstrapServerConfigDecoder: ConfigDecoder[String, BootstrapServer] =
    ConfigDecoder.lift(string =>
      BootstrapServer.from(string) match
        case Some(value) => Right(value)
        case None => Left(ConfigError("Invalid bootstrap server")),
    )
