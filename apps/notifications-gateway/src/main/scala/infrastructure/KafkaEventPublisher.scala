package es.eriktorr.notifications_engine
package infrastructure

import domain.EventPublisher

import cats.implicits.*
import cats.effect.IO
import fs2.kafka.{KafkaProducer, ProducerRecord, ProducerRecords}
import org.typelevel.log4cats.Logger

final class KafkaEventPublisher(
    producer: KafkaProducer[IO, String, Event],
    topic: String,
    logger: Logger[IO],
) extends EventPublisher:
  override def publish(event: Event): IO[Unit] =
    IO.unit <* producer
      .produce(ProducerRecords.one(ProducerRecord(topic, event.id.value, event)))
      .handleErrorWith { case error: Throwable =>
        logger.error(error)("The event could not be published") *> IO.raiseError(error)
      }
