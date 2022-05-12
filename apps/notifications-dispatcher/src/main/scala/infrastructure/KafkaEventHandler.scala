package es.eriktorr.notification_engine
package infrastructure

import Channel.{Email, Sms, Webhook}
import Message.{EmailMessage, SmsMessage, WebhookMessage}
import domain.{EventHandler, MessageDispatcher}

import cats.effect.IO
import fs2.Stream
import fs2.kafka.{commitBatchWithin, KafkaConsumer}

import scala.concurrent.duration.*

final class KafkaEventHandler(
    consumer: KafkaConsumer[IO, String, Event],
    messageDispatcher: MessageDispatcher,
) extends EventHandler:
  def handle: Stream[IO, Unit] = consumer.stream
    .mapAsync(16) { committable =>
      val event = committable.record.value
      (event.channel match
        case Email => messageDispatcher.dispatch[EmailMessage](???)
        case Sms => messageDispatcher.dispatch[SmsMessage](???)
        case Webhook => messageDispatcher.dispatch[WebhookMessage](???)
      ).as(committable.offset)
    }
    .through(commitBatchWithin(500, 15.seconds))
