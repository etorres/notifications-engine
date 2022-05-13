package es.eriktorr.notifications_engine
package infrastructure

import cats.effect.IO
import fs2.kafka.{KafkaConsumer, KafkaProducer}
import munit.{CatsEffectSuite, ScalaCheckEffectSuite}
import org.scalacheck.Test

abstract class KafkaClientsSuite extends CatsEffectSuite with ScalaCheckEffectSuite:
  override def scalaCheckTestParameters: Test.Parameters =
    super.scalaCheckTestParameters.withMinSuccessfulTests(1).withWorkers(1)

  val kafkaClientsFixture
      : Fixture[(KafkaConsumer[IO, String, Event], KafkaProducer[IO, String, Event])] =
    ResourceSuiteLocalFixture("kafka-clients", KafkaClients.defaultKafkaClients)

  override def munitFixtures: Seq[Fixture[?]] = List(kafkaClientsFixture)
