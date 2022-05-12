package es.eriktorr.notification_engine

import Message.{EmailMessage, SmsMessage, WebhookMessage}

import cats.syntax.functor.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import io.circe.{Decoder, Encoder}

trait MessageJson extends EmailMessageJson with SmsMessageJson with WebhookMessageJson:
  @SuppressWarnings(Array("org.wartremover.warts.IterableOps"))
  implicit val messageDecoder: Decoder[Message] = List[Decoder[Message]](
    Decoder[EmailMessage].widen,
    Decoder[SmsMessage].widen,
    Decoder[WebhookMessage].widen,
  ).reduceLeft(_ or _)

  implicit val messageEncoder: Encoder[Message] = Encoder.instance {
    case emailMessage @ EmailMessage(_, _, _, _) => emailMessage.asJson
    case smsMessage @ SmsMessage(_, _, _) => smsMessage.asJson
    case webhookMessage @ WebhookMessage(_, _, _, _) => webhookMessage.asJson
  }
