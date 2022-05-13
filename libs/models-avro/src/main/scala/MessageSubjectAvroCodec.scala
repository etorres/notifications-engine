package es.eriktorr.notification_engine

import cats.syntax.either.*
import vulcan.{AvroError, Codec}

trait MessageSubjectAvroCodec:
  implicit val messageSubjectAvroCodec: Codec[MessageSubject] =
    Codec.string
      .imapError(MessageSubject.from(_).leftMap(error => AvroError(error.message)))(_.value)
