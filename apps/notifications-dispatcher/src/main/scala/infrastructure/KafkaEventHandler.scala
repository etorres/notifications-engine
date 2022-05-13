package es.eriktorr.notification_engine
package infrastructure

import Event.{EmailSent, SmsSent, WebhookSent}
import domain.{EventHandler, MessageDispatcher}

import cats.effect.IO
import fs2.Stream
import fs2.kafka.{commitBatchWithin, KafkaConsumer}

import scala.concurrent.duration.*

final class KafkaEventHandler(
    consumer: KafkaConsumer[IO, String, Event],
    messageDispatcher: MessageDispatcher,
) extends EventHandler
    with MessageJsonCodec:
  def handle: Stream[IO, Unit] = consumer.stream
    .mapAsync(16) { committable =>
      val event = committable.record.value
      val message = event match
        case emailSent: EmailSent => emailSent.emailMessage
        case smsSent: SmsSent => smsSent.smsMessage
        case webhookSent: WebhookSent => webhookSent.webhookMessage
      messageDispatcher.dispatch(message).as(committable.offset)
    }
    .through(commitBatchWithin(500, 15.seconds))
