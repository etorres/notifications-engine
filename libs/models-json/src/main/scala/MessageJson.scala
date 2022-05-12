package es.eriktorr.notification_engine

import Message.{EmailMessage, SmsMessage, WebhookMessage}

import io.circe.{Decoder, Encoder, HCursor, Json}

trait MessageJson extends EmailMessageJson with SmsMessageJson with WebhookMessageJson:
  implicit def messageDecoder[A <: Message]: Decoder[A] = (cursor: HCursor) =>
    for
      media <- cursor.downField("media").as[String]
      _ = media match
        case "email" => cursor.downField("content").as[EmailMessage](emailMessageDecoder)
        case "sms" => ???
        case "webhook" => ???
        case _ => ???
    yield ???

  implicit def messageEncoder[A <: Message]: Encoder[A] = (a: A) =>
    val (media, content) = a match
      case emailMessage: EmailMessage => ("email", emailMessageEncoder(emailMessage))
      case smsMessage: SmsMessage => ("sms", smsMessageEncoder(smsMessage))
      case webhookMessage: WebhookMessage => ("webhook", webhookMessageEncoder(webhookMessage))
    Json.obj(("media", Json.fromString(media)), ("content", content))
