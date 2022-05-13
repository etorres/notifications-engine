package es.eriktorr.notifications_engine

import NotificationError.InvalidMessageBody

opaque type MessageBody = String

object MessageBody:
  private[this] def unsafeFrom(value: String): MessageBody = value

  def from(value: String): Either[InvalidMessageBody, MessageBody] = if value.nonEmpty then
    Right(unsafeFrom(value))
  else Left(InvalidMessageBody("Message body cannot be empty"))

  extension (messageBody: MessageBody) def value: String = messageBody
