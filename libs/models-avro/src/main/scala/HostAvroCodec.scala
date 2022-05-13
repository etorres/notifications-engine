package es.eriktorr.notification_engine

import com.comcast.ip4s.Host
import vulcan.{AvroError, Codec}

trait HostAvroCodec:
  implicit val hostAvroCodec: Codec[Host] = Codec.string
    .imapError(Host.fromString(_) match
      case Some(host) => Right(host)
      case None => Left(AvroError("Invalid host")),
    )(_.toString)
