package es.eriktorr.notifications_engine

import infrastructure.KafkaClients

import cats.effect.{IO, Resource}
import fs2.kafka.KafkaConsumer

final case class NotificationsDispatcherResources(kafkaConsumer: KafkaConsumer[IO, String, Event])

object NotificationsDispatcherResources:
  def impl(config: NotificationsDispatcherConfig): Resource[IO, NotificationsDispatcherResources] =
    KafkaClients.eventConsumerUsing(config.kafkaConfig).map(NotificationsDispatcherResources(_))
