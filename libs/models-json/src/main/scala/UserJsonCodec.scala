package es.eriktorr.notification_engine

import User.Role

import io.circe.{Decoder, Encoder, Json}

trait UserJsonCodec extends StringFieldDecoder:
  implicit def userJsonDecoder[A <: Role]: Decoder[User[A]] = decode[User[A]]("user", User.from[A])

  implicit def userJsonEncoder[A <: Role]: Encoder[User[A]] = (user: User[A]) =>
    Json.obj(("user", Json.fromString(user.value)))
