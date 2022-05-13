package es.eriktorr.notification_engine

import Event.{EmailSent, SmsSent, WebhookSent}

import cats.syntax.all.*
import vulcan.Codec

trait EventAvroCodec extends EventIdAvroCodec with MessageAvroCodec:
  implicit val emailSentAvroCodec: Codec[EmailSent] =
    Codec.record(
      name = "EmailSent",
      namespace = AvroNamespaces.default,
    ) { field =>
      (
        field("id", _.id),
        field("emailMessage", _.emailMessage),
      ).mapN(EmailSent(_, _))
    }

  implicit val smsSentAvroCodec: Codec[SmsSent] =
    Codec.record(
      name = "SmsSent",
      namespace = AvroNamespaces.default,
    ) { field =>
      (
        field("id", _.id),
        field("smsMessage", _.smsMessage),
      ).mapN(SmsSent(_, _))
    }

  implicit val webhookSentAvroCodec: Codec[WebhookSent] =
    Codec.record(
      name = "WebhookSent",
      namespace = AvroNamespaces.default,
    ) { field =>
      (
        field("id", _.id),
        field("webhookSent", _.webhookMessage),
      ).mapN(WebhookSent(_, _))
    }

  implicit val eventAvroCodec: Codec[Event] =
    Codec.union[Event](alt => alt[EmailSent] |+| alt[SmsSent] |+| alt[WebhookSent])