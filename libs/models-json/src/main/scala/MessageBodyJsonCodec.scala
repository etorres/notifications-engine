package es.eriktorr.notifications_engine

import io.circe.{Decoder, Encoder, Json}

trait MessageBodyJsonCodec extends StringFieldDecoder:
  implicit val messageBodyJsonDecoder: Decoder[MessageBody] =
    decodeValue[MessageBody](MessageBody.from)

  implicit val messageBodyJsonEncoder: Encoder[MessageBody] = (messageBody: MessageBody) =>
    Json.fromString(messageBody.value)
