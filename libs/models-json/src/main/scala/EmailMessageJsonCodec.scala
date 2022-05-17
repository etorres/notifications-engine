package es.eriktorr.notifications_engine

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
      subject <- cursor.downField("body").as[MessageSubject]
      body <- cursor.downField("body").as[MessageBody]
      from <- cursor.downField("from").as[User[Sender]]
      to <- cursor.downField("to").as[User[Addressee]]
    yield EmailMessage(subject, body, from, to)

  implicit val emailMessageJsonEncoder: Encoder[EmailMessage] = deriveEncoder[EmailMessage]
