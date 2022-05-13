package es.eriktorr.notification_engine

import io.circe.{Decoder, Encoder, Json}

import java.net.URL
import scala.util.Try

trait UrlJsonCodec extends StringFieldDecoder:
  implicit val urlJsonDecoder: Decoder[URL] = decodeValue[URL](str => Try(new URL(str)).toEither)

  implicit val urlJsonEncoder: Encoder[URL] = (url: URL) => Json.fromString(url.toString)
