package es.eriktorr.notifications_engine

import Message.{EmailMessage, SmsMessage, WebhookMessage}

import cats.syntax.all.*
import vulcan.Codec

trait MessageAvroCodec
    extends HostAvroCodec
    with MessageBodyAvroCodec
    with MessageSubjectAvroCodec
    with PortAvroCodec
    with UrlAvroCodec
    with UserAvroCodec:
  implicit val emailMessageAvroCodec: Codec[EmailMessage] =
    Codec.record(
      name = "EmailMessage",
      namespace = AvroNamespaces.default,
    ) { field =>
      (
        field("subject", _.subject),
        field("body", _.body),
        field("from", _.from),
        field("to", _.to),
      ).mapN(EmailMessage(_, _, _, _))
    }

  implicit val smsMessageAvroCodec: Codec[SmsMessage] =
    Codec.record(
      name = "SmsMessage",
      namespace = AvroNamespaces.default,
    ) { field =>
      (
        field("body", _.body),
        field("from", _.from),
        field("to", _.to),
      ).mapN(SmsMessage(_, _, _))
    }

  implicit val webhookMessageAvroCodec: Codec[WebhookMessage] =
    Codec.record(
      name = "WebhookMessage",
      namespace = AvroNamespaces.default,
    ) { field =>
      (
        field("body", _.body),
        field("host", _.host),
        field("port", _.port),
        field("hookUrl", _.hookUrl),
      ).mapN(WebhookMessage(_, _, _, _))
    }

  implicit val messageAvroCodec: Codec[Message] =
    Codec.union[Message](alt => alt[EmailMessage] |+| alt[SmsMessage] |+| alt[WebhookMessage])
