package es.eriktorr.notification_engine
package infrastructure

import config.KafkaConfig

import cats.effect.{IO, Resource}
import fs2.kafka.vulcan.{avroDeserializer, AvroSettings, SchemaRegistryClientSettings}
import fs2.kafka.{AutoOffsetReset, ConsumerSettings, KafkaConsumer, RecordDeserializer}

object KafkaClient extends EventCodec:
  def consumerWith(kafkaConfig: KafkaConfig): Resource[IO, KafkaConsumer[IO, String, Event]] =
    val avroSettings = AvroSettings {
      SchemaRegistryClientSettings[IO](kafkaConfig.schemaRegistry.value)
    }

    implicit def eventDeserializer: RecordDeserializer[IO, Event] =
      avroDeserializer[Event].using(avroSettings)

    val consumerSettings =
      ConsumerSettings[IO, String, Event]
        .withAutoOffsetReset(AutoOffsetReset.Earliest)
        .withBootstrapServers(kafkaConfig.bootstrapServersAsString)
        .withGroupId(kafkaConfig.consumerGroup.value)

    KafkaConsumer
      .resource(consumerSettings)
      .evalTap(_.subscribeTo(kafkaConfig.topic.value))
