package es.eriktorr.notification_engine

final case class Event(id: EventId, channel: Channel, payload: Payload)
