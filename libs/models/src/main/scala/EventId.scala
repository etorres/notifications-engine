package es.eriktorr.notification_engine

import NotificationError.InvalidEventId

import java.util.UUID

opaque type EventId = String

object EventId:
  private[this] def unsafeFrom(value: String): EventId = value

  def from(value: String): Either[InvalidEventId, EventId] = if value.nonEmpty then
    Right(unsafeFrom(value))
  else Left(InvalidEventId("Event Id cannot be empty"))

  def from(uuid: UUID): EventId = unsafeFrom(uuid.toString)

  extension (eventId: EventId) def value: String = eventId
