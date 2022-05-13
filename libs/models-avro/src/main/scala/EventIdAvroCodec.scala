package es.eriktorr.notification_engine

import cats.syntax.either.*
import vulcan.{AvroError, Codec}

trait EventIdAvroCodec:
  implicit val eventIdAvroCodec: Codec[EventId] =
    Codec.string.imapError(EventId.from(_).leftMap(error => AvroError(error.message)))(_.value)
