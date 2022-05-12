package es.eriktorr.notification_engine

import domain.MessageSender

import cats.effect.{IO, Resource}

final case class NotificationsGatewayResources(messageSender: MessageSender)

object NotificationsGatewayResources:
  val impl: Resource[IO, NotificationsGatewayResources] = ???
