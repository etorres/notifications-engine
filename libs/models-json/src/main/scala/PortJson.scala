package es.eriktorr.notification_engine

import com.comcast.ip4s.Port
import io.circe.{Decoder, Encoder, Json}

trait PortJson extends StringFieldDecoder:
  implicit val portDecoder: Decoder[Port] = decodeValue[Port] { str =>
    Port.fromString(str) match
      case Some(port) => Right(port)
      case None => Left(IllegalArgumentException("Invalid port"))
  }

  implicit val portEncoder: Encoder[Port] = (port: Port) => Json.fromString(port.toString)
