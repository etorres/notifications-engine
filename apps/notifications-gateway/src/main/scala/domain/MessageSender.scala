package es.eriktorr.notifications_engine
package domain

import Event.{EmailSent, SmsSent, WebhookSent}
import Message.{EmailMessage, SmsMessage, WebhookMessage}

import cats.effect.IO

import java.util.UUID

trait MessageSender:
  def send(message: Message): IO[EventId]

object MessageSender:
  def impl(eventPublisher: EventPublisher): MessageSender = (message: Message) =>
    for
      eventId <- IO.delay(EventId.from(UUID.randomUUID().nn))
      event = message match
        case emailMessage: EmailMessage => EmailSent(eventId, emailMessage)
        case smsMessage: SmsMessage => SmsSent(eventId, smsMessage)
        case webhookMessage: WebhookMessage => WebhookSent(eventId, webhookMessage)
      _ <- eventPublisher.publish(event)
    yield eventId
