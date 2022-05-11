package es.eriktorr.notification_engine

import vulcan.{AvroError, Codec}

trait ChannelCodec:
  implicit val channelCodec: Codec[Channel] = Codec.enumeration[Channel](
    name = "Channel",
    namespace = Namespaces.default,
    doc = Some("A notification channel"),
    symbols = List("email", "sms", "webhook"),
    encode = {
      case Channel.Email => "email"
      case Channel.Sms => "sms"
      case Channel.Webhook => "webhook"
    },
    decode = {
      case "email" => Right(Channel.Email)
      case "sms" => Right(Channel.Sms)
      case "webhook" => Right(Channel.Webhook)
      case other => Left(AvroError(s"$other is not a channel"))
    },
    default = Some(Channel.Email),
  )
