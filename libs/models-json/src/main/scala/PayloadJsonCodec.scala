package es.eriktorr.notification_engine

import io.circe.{Decoder, Encoder, Json}

trait PayloadJsonCodec extends StringFieldDecoder:
  implicit val payloadJsonDecoder: Decoder[Payload] = decodeValue[Payload](Payload.from)

  implicit val payloadJsonEncoder: Encoder[Payload] = (payload: Payload) =>
    Json.fromString(payload.value)
