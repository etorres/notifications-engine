package es.eriktorr.notifications_engine
package integration

import Event.{EmailSent, SmsSent, WebhookSent}
import Generators.eventGen
import config.KafkaConfig
import infrastructure.{
  DispatchedMessagesState,
  FakeMessageDispatcher,
  KafkaClientsSuite,
  KafkaEventHandler,
}
import integration.KafkaEventHandlerIntegrationTest.messageFrom

import cats.effect.{IO, Ref}
import fs2.kafka.{ProducerRecord, ProducerRecords}
import org.scalacheck.effect.PropF.{effectOfPropFToPropF, forAllF}

import scala.concurrent.duration.*

final class KafkaEventHandlerIntegrationTest extends KafkaClientsSuite:

  test("it should handle events") {
    forAllF(eventGen) { event =>
      val (consumer, producer) = kafkaClientsFixture()
      for
        dispatchedMessagesStateRef <- Ref.of[IO, DispatchedMessagesState](
          DispatchedMessagesState.empty,
        )
        messageDispatcher = FakeMessageDispatcher(dispatchedMessagesStateRef)
        _ <- producer.produce(
          ProducerRecords.one(
            ProducerRecord(KafkaConfig.default.topic.value, event.id.value, event),
          ),
        )
        eventHandler = KafkaEventHandler(consumer, messageDispatcher)
        _ <- eventHandler.handle.timeout(30.seconds).take(1).compile.drain
        finalState <- dispatchedMessagesStateRef.get
      yield assertEquals(finalState.messages, List(messageFrom(event)))
    }
  }

object KafkaEventHandlerIntegrationTest:
  def messageFrom(event: Event): Message = event match
    case EmailSent(_, emailMessage) => emailMessage
    case SmsSent(_, smsMessage) => smsMessage
    case WebhookSent(_, webhookMessage) => webhookMessage
