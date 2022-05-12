package es.eriktorr.notification_engine
package infrastructure

import domain.EventPublisher

import cats.effect.IO
import fs2.kafka.{KafkaProducer, ProducerRecord, ProducerRecords}

final class KafkaEventPublisher(
    producer: KafkaProducer[IO, String, Event],
    topic: String,
) extends EventPublisher:
  override def publish(event: Event): IO[Unit] =
    IO.unit <* producer.produce(ProducerRecords.one(ProducerRecord(topic, event.id.value, event)))
