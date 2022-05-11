package es.eriktorr.notification_engine

import io.circe.{Decoder, Encoder, Json}

trait MessageBodyJson extends StringFieldDecoder:
  implicit val messageBodyDecoder: Decoder[MessageBody] = decodeValue[MessageBody](MessageBody.from)

  implicit val messageBodyEncoder: Encoder[MessageBody] = (messageBody: MessageBody) =>
    Json.fromString(messageBody.value)
