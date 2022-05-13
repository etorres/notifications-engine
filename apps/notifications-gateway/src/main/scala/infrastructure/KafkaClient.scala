package es.eriktorr.notification_engine
package infrastructure

import config.KafkaConfig

import cats.effect.{IO, Resource}
import fs2.kafka.vulcan.{avroSerializer, AvroSettings, SchemaRegistryClientSettings}
import fs2.kafka.{KafkaProducer, ProducerSettings, RecordSerializer}

object KafkaClient extends EventAvroCodec:
  def producerWith(kafkaConfig: KafkaConfig): Resource[IO, KafkaProducer[IO, String, Event]] =
    val avroSettings = AvroSettings {
      SchemaRegistryClientSettings[IO](kafkaConfig.schemaRegistry.value)
    }

    implicit val eventSerializer: RecordSerializer[IO, Event] =
      avroSerializer[Event].using(avroSettings)

    val producerSettings =
      ProducerSettings[IO, String, Event].withBootstrapServers(kafkaConfig.bootstrapServersAsString)

    KafkaProducer.resource(producerSettings)
