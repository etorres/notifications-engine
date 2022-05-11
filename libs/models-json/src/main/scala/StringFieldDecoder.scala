package es.eriktorr.notification_engine

import io.circe.{Decoder, DecodingFailure, HCursor}

trait StringFieldDecoder:
  private[notification_engine] def decode[A](
      field: String,
      fA: String => Either[? <: Throwable, A],
  ): Decoder[A] =
    (cursor: HCursor) => cursor.downField(field).as[String].flatMap(valueOf(_, fA))

  private[notification_engine] def decodeValue[A](
      fA: String => Either[? <: Throwable, A],
  ): Decoder[A] =
    (cursor: HCursor) => cursor.as[String].flatMap(valueOf(_, fA))

  private[this] def valueOf[A](
      str: String,
      fA: String => Either[? <: Throwable, A],
  ): Decoder.Result[A] =
    fA(str) match
      case Left(error) => Left(DecodingFailure.fromThrowable(error, List.empty))
      case Right(value) => Right(value)
