package es.eriktorr.notifications_engine

import com.comcast.ip4s.Port
import io.circe.{Decoder, Encoder, Json}

trait PortJsonCodec extends StringFieldDecoder:
  implicit val portJsonDecoder: Decoder[Port] = decodeValue[Port] { str =>
    Port.fromString(str) match
      case Some(port) => Right(port)
      case None => Left(IllegalArgumentException("Invalid port"))
  }

  implicit val portJsonEncoder: Encoder[Port] = (port: Port) => Json.fromString(port.toString)
