package es.eriktorr.notification_engine

import io.circe.{Decoder, Encoder, Json}

trait EventIdJson extends StringFieldDecoder:
  implicit val eventIdDecoder: Decoder[EventId] = decodeValue[EventId](EventId.from)

  implicit val eventIdEncoder: Encoder[EventId] = (eventId: EventId) =>
    Json.fromString(eventId.value)
