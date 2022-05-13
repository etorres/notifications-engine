package es.eriktorr.notification_engine

import io.circe.{Decoder, Encoder, Json}

trait MessageSubjectJsonCodec extends StringFieldDecoder:
  implicit val messageSubjectJsonDecoder: Decoder[MessageSubject] =
    decodeValue[MessageSubject](MessageSubject.from)

  implicit val messageSubjectJsonEncoder: Encoder[MessageSubject] =
    (messageSubject: MessageSubject) => Json.fromString(messageSubject.value)
