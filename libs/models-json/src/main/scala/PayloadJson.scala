package es.eriktorr.notification_engine

import io.circe.{Decoder, Encoder, Json}

trait PayloadJson extends StringFieldDecoder:
  implicit val payloadDecoder: Decoder[Payload] = decodeValue[Payload](Payload.from)

  implicit val payloadEncoder: Encoder[Payload] = (payload: Payload) =>
    Json.fromString(payload.value)
