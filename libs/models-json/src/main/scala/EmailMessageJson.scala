package es.eriktorr.notification_engine

import Message.EmailMessage
import User.{Addressee, Sender}

import io.circe.*
import io.circe.generic.semiauto.*

trait EmailMessageJson extends MessageBodyJson with MessageSubjectJson with UserJson:
  implicit val emailMessageDecoder: Decoder[EmailMessage] = (cursor: HCursor) =>
    for
      body <- cursor.downField("body").as[MessageBody]
      subject <- cursor.downField("body").as[MessageSubject]
      from <- cursor.downField("from").as[User[Sender]]
      to <- cursor.downField("from").as[User[Addressee]]
    yield EmailMessage(body, subject, from, to)

  implicit val emailMessageEncoder: Encoder[EmailMessage] = deriveEncoder[EmailMessage]
