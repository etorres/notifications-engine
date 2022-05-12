package es.eriktorr.notification_engine

import io.circe.{Decoder, Encoder, Json}

import java.net.URL
import scala.util.Try

trait UrlJson extends StringFieldDecoder:
  implicit val urlDecoder: Decoder[URL] = decodeValue[URL](str => Try(new URL(str)).toEither)

  implicit val urlEncoder: Encoder[URL] = (url: URL) => Json.fromString(url.toString)
