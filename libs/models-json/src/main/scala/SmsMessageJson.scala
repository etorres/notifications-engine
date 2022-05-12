package es.eriktorr.notification_engine

import Message.SmsMessage
import User.{Addressee, Sender}

import io.circe.*
import io.circe.generic.semiauto.*

trait SmsMessageJson extends MessageBodyJson with UserJson:
  implicit val smsMessageDecoder: Decoder[SmsMessage] = (cursor: HCursor) =>
    for
      body <- cursor.downField("body").as[MessageBody]
      from <- cursor.downField("from").as[User[Sender]]
      to <- cursor.downField("to").as[User[Addressee]]
    yield SmsMessage(body, from, to)

  implicit val smsMessageEncoder: Encoder[SmsMessage] = deriveEncoder[SmsMessage]
