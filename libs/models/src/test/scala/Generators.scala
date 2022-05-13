package es.eriktorr.notifications_engine

import Event.{EmailSent, SmsSent, WebhookSent}
import Message.{EmailMessage, SmsMessage, WebhookMessage}
import User.{Addressee, Role, Sender}

import com.comcast.ip4s.{Host, Port}
import org.scalacheck.Gen

import java.net.URL

object Generators:
  private[this] def textGen(minLength: Int = 3, maxLength: Int = 10): Gen[String] = for
    length <- Gen.choose(minLength, maxLength)
    text <- Gen.listOfN[Char](length, Gen.alphaNumChar).map(_.mkString)
  yield text

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  val eventIdGen: Gen[EventId] = Gen.uuid.map(uuid => EventId.from(uuid.toString).toOption.get)

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  private[this] val hostGen: Gen[Host] = Gen
    .frequency(
      5 -> Gen.oneOf("127.0.0.1", "10.1.2.0", "::1", "::7f00:1", "ff3b::"),
      5 -> Gen.oneOf("example.org", "www.example.org"),
    )
    .map(host => Host.fromString(host).get)

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  private[this] val messageBodyGen: Gen[MessageBody] =
    textGen(1, 24).map(MessageBody.from(_).toOption.get)

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  private[this] val messageSubjectGen: Gen[MessageSubject] =
    textGen(1, 12).map(MessageSubject.from(_).toOption.get)

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  private[this] val portGen: Gen[Port] = Gen.choose(0, 65535).map(port => Port.fromInt(port).get)

  /** @see
    *   [[https://medium.com/@supermanue/building-useful-scalacheck-generators-71635d1edb9d Building useful Scalacheck Generators]]
    */
  private[this] val urlGen: Gen[String] =
    def httpTypeGen: Gen[String] = Gen.oneOf(Seq("http", "https"))
    def domainGen: Gen[String] = for
      numberOfFragments <- Gen.choose(1, 3)
      domain <- Gen.listOfN[String](numberOfFragments, textGen()).map(_.mkString("."))
    yield domain
    def domainTypeGen: Gen[String] = Gen.oneOf(Seq("com", "es", "org"))
    def pathGen: Gen[String] = for
      numberOfFragments <- Gen.choose(1, 3)
      path <- Gen.listOfN[String](numberOfFragments, textGen()).map(_.mkString("/"))
    yield path
    for
      http <- httpTypeGen
      domain <- domainGen
      domainType <- domainTypeGen
      path <- pathGen
    yield http + "://" + domain + "." + domainType + "/" + path

  @SuppressWarnings(Array("org.wartremover.warts.OptionPartial"))
  private[this] def userGen[A <: Role]: Gen[User[A]] = textGen().map(User.from[A](_).toOption.get)

  private[this] val emailMessageGen: Gen[EmailMessage] = for
    body <- messageBodyGen
    subject <- messageSubjectGen
    from <- userGen[Sender]
    to <- userGen[Addressee]
  yield EmailMessage(body, subject, from, to)

  private[this] val smsMessageGen: Gen[SmsMessage] = for
    body <- messageBodyGen
    from <- userGen[Sender]
    to <- userGen[Addressee]
  yield SmsMessage(body, from, to)

  private[this] val webhookMessageGen: Gen[WebhookMessage] = for
    body <- messageBodyGen
    host <- hostGen
    port <- portGen
    hookUrl <- urlGen.map(URL(_))
  yield WebhookMessage(body, host, port, hookUrl)

  val messageGen: Gen[Message] = Gen.oneOf(emailMessageGen, smsMessageGen, webhookMessageGen)

  val eventGen: Gen[Event] = for
    eventId <- eventIdGen
    message <- messageGen
    event = message match
      case emailMessage: EmailMessage => EmailSent(eventId, emailMessage)
      case smsMessage: SmsMessage => SmsSent(eventId, smsMessage)
      case webhookMessage: WebhookMessage => WebhookSent(eventId, webhookMessage)
  yield event
