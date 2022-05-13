package es.eriktorr.notifications_engine

import NotificationError.InvalidMessageSubject

opaque type MessageSubject = String

object MessageSubject:
  private[this] def unsafeFrom(value: String): MessageSubject = value

  def from(value: String): Either[InvalidMessageSubject, MessageSubject] = if value.nonEmpty then
    Right(unsafeFrom(value))
  else Left(InvalidMessageSubject("Message subject cannot be empty"))

  extension (messageSubject: MessageSubject) def value: String = messageSubject
