package es.eriktorr.notification_engine

import vulcan.{AvroError, Codec}

import java.net.URL
import scala.util.Try

trait UrlAvroCodec:
  implicit val urlAvroCodec: Codec[URL] =
    Codec.string.imapTry(str => Try(new URL(str).nn))(_.toString)
