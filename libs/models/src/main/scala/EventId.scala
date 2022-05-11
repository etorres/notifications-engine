package es.eriktorr.notification_engine

import NotificationError.InvalidEventId

opaque type EventId = String

object EventId:

  def unsafeFrom(value: String): EventId = value

  def from(value: String): Either[InvalidEventId, EventId] = if value.nonEmpty then
    Right(unsafeFrom(value))
  else Left(InvalidEventId("Event Id cannot be empty"))

  extension (eventId: EventId) def value: String = eventId
