package es.eriktorr.notification_engine

import User.{Addressee, Sender}

import com.comcast.ip4s.{Host, Port}

import java.net.URL

sealed trait Message

object Message:
  final case class EmailMessage(
      body: MessageBody,
      subject: MessageSubject,
      from: User[Sender],
      to: User[Addressee],
  ) extends Message

  final case class SmsMessage(body: MessageBody, from: User[Sender], to: User[Addressee])
      extends Message

  final case class WebhookMessage(payload: Payload, host: Host, port: Port, hookUrl: URL)
      extends Message
