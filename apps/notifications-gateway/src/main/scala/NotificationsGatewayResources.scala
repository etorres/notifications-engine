package es.eriktorr.notification_engine

import domain.MessageSender
import infrastructure.KafkaClient

import cats.effect.{IO, Resource}
import fs2.kafka.KafkaProducer

final case class NotificationsGatewayResources(kafkaProducer: KafkaProducer[IO, String, Event])

object NotificationsGatewayResources:
  def impl(config: NotificationsGatewayConfig): Resource[IO, NotificationsGatewayResources] =
    KafkaClient.producerWith(config.kafkaConfig).map(NotificationsGatewayResources(_))
