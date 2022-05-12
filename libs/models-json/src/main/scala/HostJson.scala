package es.eriktorr.notification_engine

import com.comcast.ip4s.Host
import io.circe.{Decoder, Encoder, Json}

trait HostJson extends StringFieldDecoder:
  implicit val hostDecoder: Decoder[Host] = decodeValue[Host] { str =>
    Host.fromString(str) match
      case Some(host) => Right(host)
      case None => Left(IllegalArgumentException("Invalid host"))
  }

  implicit val hostEncoder: Encoder[Host] = (host: Host) => Json.fromString(host.toString)
