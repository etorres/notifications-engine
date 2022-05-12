package es.eriktorr.notification_engine

import config.KafkaConfig.ConsumerGroup

import ciris.{ConfigDecoder, ConfigError}

trait ConsumerGroupConfigDecoder:
  implicit def consumerGroupConfigDecoder: ConfigDecoder[String, ConsumerGroup] =
    ConfigDecoder.lift(string =>
      ConsumerGroup.from(string) match
        case Some(value) => Right(value)
        case None => Left(ConfigError("Invalid consumer group")),
    )
