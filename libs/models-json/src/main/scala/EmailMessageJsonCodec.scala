package es.eriktorr.notification_engine

import Message.EmailMessage
import User.{Addressee, Sender}

import io.circe.*
import io.circe.generic.semiauto.*

trait EmailMessageJsonCodec
    extends MessageBodyJsonCodec
    with MessageSubjectJsonCodec
    with UserJsonCodec:
  implicit val emailMessageJsonDecoder: Decoder[EmailMessage] = (cursor: HCursor) =>
    for
      body <- cursor.downField("body").as[MessageBody]
      subject <- cursor.downField("body").as[MessageSubject]
      from <- cursor.downField("from").as[User[Sender]]
      to <- cursor.downField("to").as[User[Addressee]]
    yield EmailMessage(body, subject, from, to)

  implicit val emailMessageJsonEncoder: Encoder[EmailMessage] = deriveEncoder[EmailMessage]
