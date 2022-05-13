package es.eriktorr.notifications_engine
package integration

import Generators.eventGen
import config.KafkaConfig
import infrastructure.{KafkaClientsSuite, KafkaEventPublisher}
import integration.KafkaEventPublisherIntegrationTest.{ConsumedEventsState, InMemoryConsumedEvents}

import cats.effect.{IO, Ref, Resource}
import cats.implicits.*
import fs2.kafka.{commitBatchWithin, KafkaConsumer, KafkaProducer}
import org.scalacheck.effect.PropF.{effectOfPropFToPropF, forAllF}
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.duration.*

final class KafkaEventPublisherIntegrationTest extends KafkaClientsSuite:

  test("it should publish events to a topic in kafka") {
    forAllF(eventGen) { event =>
      val (consumer, producer) = kafkaClientsFixture()
      for
        consumedEventsStateRef <- Ref.of[IO, ConsumedEventsState](ConsumedEventsState.empty)
        consumedEvents = InMemoryConsumedEvents(consumedEventsStateRef)
        logger <- Slf4jLogger.create[IO]
        eventPublisher = KafkaEventPublisher(producer, KafkaConfig.default.topic.value, logger)
        _ <- eventPublisher.publish(event)
        _ <- consumer.stream
          .mapAsync(1) { committable =>
            consumedEvents.add(committable.record.value).as(committable.offset)
          }
          .through(commitBatchWithin(500, 15.seconds))
          .timeout(30.seconds)
          .take(1)
          .compile
          .drain
        finalState <- consumedEventsStateRef.get
      yield assertEquals(finalState.events, List(event))
    }
  }

object KafkaEventPublisherIntegrationTest:
  final private case class ConsumedEventsState(events: List[Event])

  private object ConsumedEventsState:
    def empty: ConsumedEventsState = ConsumedEventsState(List.empty)

  final private class InMemoryConsumedEvents(stateRef: Ref[IO, ConsumedEventsState]):
    def add(event: Event): IO[Unit] =
      stateRef.update(currentState => currentState.copy(event :: currentState.events))
