package es.eriktorr.notification_engine

import io.circe.{Decoder, Encoder, Json}

trait MessageSubjectJson extends StringFieldDecoder:
  implicit val messageSubjectDecoder: Decoder[MessageSubject] =
    decodeValue[MessageSubject](MessageSubject.from)

  implicit val messageSubjectEncoder: Encoder[MessageSubject] = (messageSubject: MessageSubject) =>
    Json.fromString(messageSubject.value)
