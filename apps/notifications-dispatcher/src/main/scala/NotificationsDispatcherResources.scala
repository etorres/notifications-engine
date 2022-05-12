package es.eriktorr.notification_engine

import infrastructure.KafkaClient

import cats.effect.{IO, Resource}
import fs2.kafka.KafkaConsumer

final case class NotificationsDispatcherResources(kafkaConsumer: KafkaConsumer[IO, String, Event])

object NotificationsDispatcherResources:
  def impl(config: NotificationsDispatcherConfig): Resource[IO, NotificationsDispatcherResources] =
    KafkaClient.consumerWith(config.kafkaConfig).map(NotificationsDispatcherResources(_))
