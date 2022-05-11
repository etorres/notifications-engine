package es.eriktorr.notification_engine

import cats.syntax.either.*
import vulcan.{AvroError, Codec}

object EventIdCodec:

  implicit val eventIdCodec: Codec[EventId] =
    Codec[String].imapError(str => EventId.from(str).leftMap(error => AvroError(error.message)))(
      eventId => eventId.value,
    )
