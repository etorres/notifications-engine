package es.eriktorr.notification_engine

import cats.syntax.either.*
import vulcan.{AvroError, Codec}

object PayloadCodec:
  implicit val payloadCodec: Codec[Payload] =
    Codec.string.imapError(Payload.from(_).leftMap(error => AvroError(error.message)))(_.value)
