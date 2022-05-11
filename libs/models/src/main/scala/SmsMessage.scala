package es.eriktorr.notification_engine

import User.{Addressee, Sender}

final case class SmsMessage(body: MessageBody, from: User[Sender], to: User[Addressee])
