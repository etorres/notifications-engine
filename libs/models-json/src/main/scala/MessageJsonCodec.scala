package es.eriktorr.notifications_engine

import Message.{EmailMessage, SmsMessage, WebhookMessage}

import cats.syntax.functor.*
import io.circe.generic.auto.*
import io.circe.syntax.*
import io.circe.{Decoder, Encoder}

trait MessageJsonCodec
    extends EmailMessageJsonCodec
    with SmsMessageJsonCodec
    with WebhookMessageJsonCodec:
  @SuppressWarnings(Array("org.wartremover.warts.IterableOps"))
  implicit val messageJsonDecoder: Decoder[Message] = List[Decoder[Message]](
    Decoder[EmailMessage].widen,
    Decoder[SmsMessage].widen,
    Decoder[WebhookMessage].widen,
  ).reduceLeft(_ or _)

  implicit val messageJsonEncoder: Encoder[Message] = Encoder.instance {
    case emailMessage @ EmailMessage(_, _, _, _) => emailMessage.asJson
    case smsMessage @ SmsMessage(_, _, _) => smsMessage.asJson
    case webhookMessage @ WebhookMessage(_, _, _, _) => webhookMessage.asJson
  }
