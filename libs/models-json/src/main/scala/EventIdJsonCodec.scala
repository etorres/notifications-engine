package es.eriktorr.notifications_engine

import io.circe.{Decoder, Encoder, Json}

trait EventIdJsonCodec extends StringFieldDecoder:
  implicit val eventIdJsonDecoder: Decoder[EventId] = decodeValue[EventId](EventId.from)

  implicit val eventIdJsonEncoder: Encoder[EventId] = (eventId: EventId) =>
    Json.fromString(eventId.value)
