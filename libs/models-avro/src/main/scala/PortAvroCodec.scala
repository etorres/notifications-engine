package es.eriktorr.notification_engine

import com.comcast.ip4s.Port
import vulcan.{AvroError, Codec}

trait PortAvroCodec:
  implicit val portAvroCodec: Codec[Port] = Codec.int
    .imapError(Port.fromInt(_) match
      case Some(host) => Right(host)
      case None => Left(AvroError("Invalid port")),
    )(_.value)
