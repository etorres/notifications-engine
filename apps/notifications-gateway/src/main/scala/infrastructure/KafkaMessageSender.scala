package es.eriktorr.notification_engine
package infrastructure

import Event.{EmailSent, SmsSent, WebhookSent}
import Message.{EmailMessage, SmsMessage, WebhookMessage}
import domain.{EventPublisher, MessageSender}

import cats.effect.IO

import java.util.UUID

final class KafkaMessageSender(eventPublisher: EventPublisher) extends MessageSender:
  override def send(message: Message): IO[EventId] = for
    eventId <- IO.delay(EventId.from(UUID.randomUUID().nn))
    event = message match
      case emailMessage: EmailMessage => EmailSent(eventId, emailMessage)
      case smsMessage: SmsMessage => SmsSent(eventId, smsMessage)
      case webhookMessage: WebhookMessage => WebhookSent(eventId, webhookMessage)
    _ <- eventPublisher.publish(event)
  yield eventId
