package es.eriktorr.notification_engine

import NotificationError.InvalidPayload

opaque type Payload = String

object Payload:

  def unsafeFrom(value: String): Payload = value

  def from(value: String): Either[InvalidPayload, Payload] = if value.nonEmpty then
    Right(unsafeFrom(value))
  else Left(InvalidPayload("Payload cannot be empty"))

  extension (payload: Payload) def value: String = payload
