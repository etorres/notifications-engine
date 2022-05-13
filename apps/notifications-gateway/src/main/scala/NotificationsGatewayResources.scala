package es.eriktorr.notifications_engine

import domain.MessageSender
import infrastructure.KafkaClients

import cats.effect.{IO, Resource}
import fs2.kafka.KafkaProducer

final case class NotificationsGatewayResources(kafkaProducer: KafkaProducer[IO, String, Event])

object NotificationsGatewayResources:
  def impl(config: NotificationsGatewayConfig): Resource[IO, NotificationsGatewayResources] =
    KafkaClients.eventProducerUsing(config.kafkaConfig).map(NotificationsGatewayResources(_))
