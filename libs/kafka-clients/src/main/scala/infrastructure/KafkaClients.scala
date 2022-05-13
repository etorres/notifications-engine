package es.eriktorr.notifications_engine
package infrastructure

import config.KafkaConfig

import cats.effect.{IO, Resource}
import cats.implicits.*
import fs2.kafka.vulcan.{
  avroDeserializer,
  avroSerializer,
  AvroSettings,
  SchemaRegistryClientSettings,
}
import fs2.kafka.*

object KafkaClients extends EventAvroCodec:
  lazy val defaultKafkaClients
      : Resource[IO, (KafkaConsumer[IO, String, Event], KafkaProducer[IO, String, Event])] =
    val kafkaConfig = KafkaConfig.default
    Resource
      .eval[IO, KafkaClients] {
        avroSettingsFrom(kafkaConfig).map { avroSettings =>
          val consumer = consumerFrom(kafkaConfig, avroSettings)
          val producer = producerFrom(kafkaConfig, avroSettings)
          KafkaClients(consumer, producer)
        }
      }
      .flatMap { clients =>
        (clients.consumer, clients.producer).tupled
      }

  def eventConsumerUsing(kafkaConfig: KafkaConfig): Resource[IO, KafkaConsumer[IO, String, Event]] =
    Resource
      .eval[IO, AvroSettings[IO]](avroSettingsFrom(kafkaConfig))
      .flatMap(consumerFrom(kafkaConfig, _))

  def eventProducerUsing(kafkaConfig: KafkaConfig): Resource[IO, KafkaProducer[IO, String, Event]] =
    Resource
      .eval[IO, AvroSettings[IO]](avroSettingsFrom(kafkaConfig))
      .flatMap(producerFrom(kafkaConfig, _))

  private[this] def avroSettingsFrom(kafkaConfig: KafkaConfig): IO[AvroSettings[IO]] =
    val avroSettingsSharedClient: IO[AvroSettings[IO]] = SchemaRegistryClientSettings[IO](
      kafkaConfig.schemaRegistry.value,
    ).createSchemaRegistryClient.map(AvroSettings(_))

    avroSettingsSharedClient
      .flatMap { avroSettings =>
        val avroSettingsWithoutAutoRegister =
          avroSettings
            .withAutoRegisterSchemas(false)
            .withProperties(
              "auto.register.schemas" -> "false",
              "use.latest.version" -> "true",
            )

        avroSettingsWithoutAutoRegister.registerSchema[String](
          s"${kafkaConfig.topic}-key",
        ) *>
          avroSettingsWithoutAutoRegister.registerSchema[Event](
            s"${kafkaConfig.topic}-value",
          ) *> IO(avroSettingsWithoutAutoRegister)
      }

  private[this] def consumerFrom(
      kafkaConfig: KafkaConfig,
      avroSettings: AvroSettings[IO],
  ): Resource[IO, KafkaConsumer[IO, String, Event]] =
    implicit val eventDeserializer: RecordDeserializer[IO, Event] =
      avroDeserializer[Event].using(avroSettings)

    val consumerSettings = ConsumerSettings[IO, String, Event]
      .withAutoOffsetReset(AutoOffsetReset.Earliest)
      .withBootstrapServers(kafkaConfig.bootstrapServersAsString)
      .withGroupId(kafkaConfig.consumerGroup.value)

    KafkaConsumer
      .resource(consumerSettings)
      .evalTap(_.subscribeTo(kafkaConfig.topic.value))

  private[this] def producerFrom(
      kafkaConfig: KafkaConfig,
      avroSettings: AvroSettings[IO],
  ) =
    implicit val eventSerializer: RecordSerializer[IO, Event] =
      avroSerializer[Event].using(avroSettings)

    val producerSettings = ProducerSettings[IO, String, Event]
      .withBootstrapServers(kafkaConfig.bootstrapServersAsString)

    KafkaProducer.resource(producerSettings)

  final private[this] case class KafkaClients(
      consumer: Resource[IO, KafkaConsumer[IO, String, Event]],
      producer: Resource[IO, KafkaProducer[IO, String, Event]],
  )
