package es.eriktorr.notification_engine
package infrastructure

import domain.MessageSender
import infrastructure.FakeMessageSender.FakeMessageSenderState

import cats.effect.{IO, Ref}

final class FakeMessageSender(stateRef: Ref[IO, FakeMessageSenderState]) extends MessageSender:
  override def send(message: Message): IO[EventId] = stateRef.modify { currentState =>
    val (head, next) = currentState.eventIds match
      case ::(head, next) => (head, next)
      case Nil => throw new IllegalStateException("Event Ids exhausted")
    (currentState.copy(next), head)
  }

object FakeMessageSender:
  final case class FakeMessageSenderState(eventIds: List[EventId]):
    def setEventIds(newEventIds: List[EventId]): FakeMessageSenderState = copy(newEventIds)

  object FakeMessageSenderState:
    def empty: FakeMessageSenderState = FakeMessageSenderState(List.empty)
