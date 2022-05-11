package es.eriktorr.notification_engine

import User.Role

import io.circe.{Decoder, Encoder, Json}

trait UserJson extends StringFieldDecoder:
  implicit def userDecoder[A <: Role]: Decoder[User[A]] = decode[User[A]]("user", User.from[A])

  implicit def userEncoder[A <: Role]: Encoder[User[A]] = (user: User[A]) =>
    Json.obj(("user", Json.fromString(user.value)))
