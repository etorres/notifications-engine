package es.eriktorr.notification_engine

import com.comcast.ip4s.Host
import io.circe.{Decoder, Encoder, Json}

trait HostJsonCodec extends StringFieldDecoder:
  implicit val hostJsonDecoder: Decoder[Host] = decodeValue[Host] { str =>
    Host.fromString(str) match
      case Some(host) => Right(host)
      case None => Left(IllegalArgumentException("Invalid host"))
  }

  implicit val hostJsonEncoder: Encoder[Host] = (host: Host) => Json.fromString(host.toString)
