package es.eriktorr.notification_engine

import User.{Addressee, Sender}

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
