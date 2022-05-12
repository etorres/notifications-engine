package es.eriktorr.notification_engine
package infrastructure

import Message.{EmailMessage, SmsMessage}
import domain.MessageSender
import infrastructure.FakeMessageSender.FakeMessageSenderState

import cats.effect.{IO, Ref}

final class FakeMessageSender(stateRef: Ref[IO, FakeMessageSenderState]) extends MessageSender:
  override def send(emailMessage: EmailMessage): IO[EventId] = sendAny

  override def send(smsMessage: SmsMessage): IO[EventId] = sendAny

  private[this] val sendAny = stateRef.modify { currentState =>
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
