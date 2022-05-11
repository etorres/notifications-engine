package es.eriktorr.notification_engine

import cats.syntax.either.*
import vulcan.{AvroError, Codec}

object PayloadCodec:

  implicit val payloadCodec: Codec[Payload] =
    Codec[String].imapError(str => Payload.from(str).leftMap(error => AvroError(error.message)))(
      eventId => eventId.value,
    )
