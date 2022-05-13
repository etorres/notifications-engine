package es.eriktorr.notification_engine

import Message.{EmailMessage, SmsMessage, WebhookMessage}

sealed trait Event:
  def id: EventId

object Event:
  final case class EmailSent(id: EventId, emailMessage: EmailMessage) extends Event

  final case class SmsSent(id: EventId, smsMessage: SmsMessage) extends Event

  final case class WebhookSent(id: EventId, webhookMessage: WebhookMessage) extends Event
