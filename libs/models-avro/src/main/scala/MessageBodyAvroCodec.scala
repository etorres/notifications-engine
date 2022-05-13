package es.eriktorr.notifications_engine

import cats.syntax.either.*
import vulcan.{AvroError, Codec}

trait MessageBodyAvroCodec:
  implicit val messageBodyAvroCodec: Codec[MessageBody] =
    Codec.string.imapError(MessageBody.from(_).leftMap(error => AvroError(error.message)))(_.value)
