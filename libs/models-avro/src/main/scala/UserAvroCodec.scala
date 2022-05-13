package es.eriktorr.notification_engine

import User.Role

import cats.syntax.either.*
import vulcan.{AvroError, Codec}

trait UserAvroCodec:
  implicit def userAvroCodec[A <: Role]: Codec[User[A]] =
    Codec.string.imapError(User.from[A](_).leftMap(error => AvroError(error.message)))(_.value)
