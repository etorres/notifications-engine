package es.eriktorr.notifications_engine

import User.{Addressee, Sender}

import com.comcast.ip4s.{Host, Port}

import java.net.URL

sealed trait Message:
  def body: MessageBody

object Message:
  final case class EmailMessage(
      subject: MessageSubject,
      body: MessageBody,
      from: User[Sender],
      to: User[Addressee],
  ) extends Message

  final case class SmsMessage(body: MessageBody, from: User[Sender], to: User[Addressee])
      extends Message

  final case class WebhookMessage(body: MessageBody, host: Host, port: Port, hookUrl: URL)
      extends Message
