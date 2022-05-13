package es.eriktorr.notification_engine
package integration

import Generators.eventGen
import config.KafkaConfig
import infrastructure.KafkaEventPublisher
import integration.KafkaEventPublisherIntegrationTest.defaultKafkaClients

import cats.effect.{IO, Resource}
import cats.implicits.*
import fs2.kafka.*
import fs2.kafka.vulcan.{
  avroDeserializer,
  avroSerializer,
  AvroSettings,
  SchemaRegistryClientSettings,
}
import munit.{CatsEffectSuite, ScalaCheckEffectSuite}
import org.scalacheck.effect.PropF.{effectOfPropFToPropF, forAllF}
import org.scalacheck.{Gen, Test}
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.duration.*

final class KafkaEventPublisherIntegrationTest extends CatsEffectSuite with ScalaCheckEffectSuite:
  override def scalaCheckTestParameters: Test.Parameters =
    super.scalaCheckTestParameters.withMinSuccessfulTests(1).withWorkers(1)

  val kafkaClientsFixture
      : Fixture[(KafkaConsumer[IO, String, Event], KafkaProducer[IO, String, Event])] =
    ResourceSuiteLocalFixture(
      "kafka-consumer-producer",
      KafkaEventPublisherIntegrationTest.defaultKafkaClients,
    )

  override def munitFixtures: Seq[Fixture[?]] = List(kafkaClientsFixture)

  test("it should publish events to a topic in kafka") {
    forAllF(eventGen) { event =>
      val (consumer, producer) = kafkaClientsFixture()
      for
        logger <- Slf4jLogger.create[IO]
        eventPublisher = KafkaEventPublisher(producer, KafkaConfig.default.topic.value, logger)
        _ <- eventPublisher.publish(event)
        result <- consumer.stream
          .mapAsync(2) { committable =>
            IO(committable.record.value)
          }
          .timeout(30.seconds)
          .take(1)
          .compile
          .toList
      yield assertEquals(result, List(event))
    }
  }

object KafkaEventPublisherIntegrationTest extends EventAvroCodec:
  lazy val defaultKafkaClients
      : Resource[IO, (KafkaConsumer[IO, String, Event], KafkaProducer[IO, String, Event])] =
    kafkaClientsFor(KafkaConfig.default)

  private[this] def kafkaClientsFor(kafkaConfig: KafkaConfig) =
    Resource
      .eval[IO, KafkaClients] {
        val avroSettingsSharedClient: IO[AvroSettings[IO]] = SchemaRegistryClientSettings[IO](
          kafkaConfig.schemaRegistry.value,
        ).createSchemaRegistryClient.map(AvroSettings(_))

        avroSettingsSharedClient
          .flatMap { avroSettings =>
            val avroSettingsWithoutAutoRegister = avroSettings.withAutoRegisterSchemas(false)

            avroSettingsWithoutAutoRegister.registerSchema[String](
              s"${kafkaConfig.topic}-key",
            ) *>
              avroSettingsWithoutAutoRegister.registerSchema[Event](
                s"${kafkaConfig.topic}-value",
              ) *> IO(avroSettingsWithoutAutoRegister)
          }
          .map { avroSettingsWithoutAutoRegister =>
            implicit def eventDeserializer: RecordDeserializer[IO, Event] =
              avroDeserializer[Event].using(avroSettingsWithoutAutoRegister)

            implicit val eventSerializer: RecordSerializer[IO, Event] =
              avroSerializer[Event].using(avroSettingsWithoutAutoRegister)

            val consumerSettings = ConsumerSettings[IO, String, Event]
              .withAutoOffsetReset(AutoOffsetReset.Earliest)
              .withBootstrapServers(kafkaConfig.bootstrapServersAsString)
              .withGroupId(kafkaConfig.consumerGroup.value)

            val producerSettings = ProducerSettings[IO, String, Event]
              .withBootstrapServers(kafkaConfig.bootstrapServersAsString)

            val consumer = KafkaConsumer
              .resource(consumerSettings)
              .evalTap(_.subscribeTo(kafkaConfig.topic.value))

            val producer = KafkaProducer.resource(producerSettings)

            KafkaClients(consumer, producer)
          }
      }
      .flatMap { clients =>
        (clients.consumer, clients.producer).tupled
      }

  final private[this] case class KafkaClients(
      consumer: Resource[IO, KafkaConsumer[IO, String, Event]],
      producer: Resource[IO, KafkaProducer[IO, String, Event]],
  )
