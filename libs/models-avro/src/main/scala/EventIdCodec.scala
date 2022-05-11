package es.eriktorr.notification_engine

import cats.syntax.either.*
import vulcan.{AvroError, Codec}

object EventIdCodec:
  implicit val eventIdCodec: Codec[EventId] =
    Codec.string.imapError(EventId.from(_).leftMap(error => AvroError(error.message)))(_.value)
