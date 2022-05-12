package es.eriktorr.notification_engine
package infrastructure

import domain.{EventPublisher, MessageSender}
import Message.{EmailMessage, SmsMessage, WebhookMessage}
import Channel.{Email, Sms, Webhook}

import cats.effect.IO

import java.util.UUID

final class KafkaMessageSender(eventPublisher: EventPublisher) extends MessageSender:
  override def send[A <: Message](message: A): IO[EventId] = for
    eventId <- IO.delay(EventId.from(UUID.randomUUID().nn))
    _ <- eventPublisher.publish(
      Event(
        eventId,
        message match
          case _: EmailMessage => Email
          case _: SmsMessage => Sms
          case _: WebhookMessage => Webhook
        ,
        Payload.from("123").toOption.get, // TODO
      ),
    )
  yield eventId
