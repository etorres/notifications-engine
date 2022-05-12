package es.eriktorr.notification_engine
package infrastructure

import domain.{EventHandler, MessageDispatcher}

import cats.effect.IO
import fs2.Stream
import fs2.kafka.{commitBatchWithin, KafkaConsumer}
import io.circe.parser

import scala.concurrent.duration.*

final class KafkaEventHandler(
    consumer: KafkaConsumer[IO, String, Event],
    messageDispatcher: MessageDispatcher,
) extends EventHandler
    with MessageJson:
  def handle: Stream[IO, Unit] = consumer.stream
    .mapAsync(16) { committable =>
      val event = committable.record.value
      (for
        message <- IO.fromEither(parser.decode[Message](event.payload.value))
        _ <- messageDispatcher.dispatch(message)
      yield ()).as(committable.offset)
    }
    .through(commitBatchWithin(500, 15.seconds))
