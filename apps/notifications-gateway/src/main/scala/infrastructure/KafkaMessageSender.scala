package es.eriktorr.notification_engine
package infrastructure

import Channel.{Email, Sms, Webhook}
import Message.{EmailMessage, SmsMessage, WebhookMessage}
import domain.{EventPublisher, MessageSender}

import cats.effect.IO
import io.circe.syntax.*

import java.util.UUID

final class KafkaMessageSender(eventPublisher: EventPublisher)
    extends MessageSender
    with MessageJson:
  override def send(message: Message): IO[EventId] = for
    payload <- IO.fromEither(Payload.from(message.asJson.noSpaces))
    eventId <- IO.delay(EventId.from(UUID.randomUUID().nn))
    _ <- eventPublisher.publish(
      Event(
        eventId,
        message match
          case _: EmailMessage => Email
          case _: SmsMessage => Sms
          case _: WebhookMessage => Webhook
        ,
        payload,
      ),
    )
  yield eventId
