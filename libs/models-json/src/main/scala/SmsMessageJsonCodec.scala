package es.eriktorr.notifications_engine

import Message.SmsMessage
import User.{Addressee, Sender}

import io.circe.*
import io.circe.generic.semiauto.*

trait SmsMessageJsonCodec extends MessageBodyJsonCodec with UserJsonCodec:
  implicit val smsMessageJsonDecoder: Decoder[SmsMessage] = (cursor: HCursor) =>
    for
      body <- cursor.downField("body").as[MessageBody]
      from <- cursor.downField("from").as[User[Sender]]
      to <- cursor.downField("to").as[User[Addressee]]
    yield SmsMessage(body, from, to)

  implicit val smsMessageJsonEncoder: Encoder[SmsMessage] = deriveEncoder[SmsMessage]
