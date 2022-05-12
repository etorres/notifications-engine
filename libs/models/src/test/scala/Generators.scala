package es.eriktorr.notification_engine

import Message.{EmailMessage, SmsMessage}
import User.{Addressee, Role, Sender}

import org.scalacheck.Gen

object Generators:
  private[this] def textGen(minLength: Int = 3, maxLength: Int = 10): Gen[String] = for
    length <- Gen.choose(minLength, maxLength)
    text <- Gen.listOfN[Char](length, Gen.alphaNumChar).map(_.mkString)
  yield text

  def eventIdGen: Gen[EventId] = Gen.uuid.map(uuid => EventId.from(uuid.toString).toOption.get)

  private[this] def messageBodyGen: Gen[MessageBody] =
    textGen(1, 24).map(MessageBody.from(_).toOption.get)

  private[this] def messageSubjectGen: Gen[MessageSubject] =
    textGen(1, 12).map(MessageSubject.from(_).toOption.get)

  private[this] def userGen[A <: Role]: Gen[User[A]] = textGen().map(User.from[A](_).toOption.get)

  private[this] def emailMessageGen: Gen[EmailMessage] = for
    body <- messageBodyGen
    subject <- messageSubjectGen
    from <- userGen[Sender]
    to <- userGen[Addressee]
  yield EmailMessage(body, subject, from, to)

  private[this] def smsMessageGen: Gen[SmsMessage] = for
    body <- messageBodyGen
    from <- userGen[Sender]
    to <- userGen[Addressee]
  yield SmsMessage(body, from, to)

  def messageGen: Gen[Message] = Gen.oneOf(emailMessageGen, smsMessageGen)
