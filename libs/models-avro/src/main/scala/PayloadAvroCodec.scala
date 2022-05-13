package es.eriktorr.notification_engine

import cats.syntax.either.*
import vulcan.{AvroError, Codec}

trait PayloadAvroCodec:
  implicit val payloadAvroCodec: Codec[Payload] =
    Codec.string.imapError(Payload.from(_).leftMap(error => AvroError(error.message)))(_.value)
