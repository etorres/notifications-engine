package es.eriktorr.notification_engine

import User.{Addressee, Sender}

import io.circe.Decoder.Result
import io.circe.{Decoder, Encoder, HCursor, Json}

trait SmsMessageJson extends MessageBodyJson with UserJson:
  implicit val smsMessageDecoder: Decoder[SmsMessage] = (cursor: HCursor) =>
    for
      body <- cursor.downField("body").as[MessageBody]
      from <- cursor.downField("from").as[User[Sender]]
      to <- cursor.downField("from").as[User[Addressee]]
    yield SmsMessage(body, from, to)

  implicit val smsMessageEncoder: Encoder[SmsMessage] = (smsMessage: SmsMessage) =>
    Json.obj(
      ("body", Json.fromString(smsMessage.body.value)),
      ("from", Json.fromString(smsMessage.from.value)),
      ("to", Json.fromString(smsMessage.to.value)),
    )
