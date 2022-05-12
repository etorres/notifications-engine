package es.eriktorr.notification_engine
package integration

import Generators.{eventIdGen, messageGen}
import Message.{EmailMessage, SmsMessage, WebhookMessage}
import User.{Addressee, Sender}
import infrastructure.FakeMessageSender.FakeMessageSenderState
import infrastructure.{FakeMessageSender, HttpServer}
import integration.HttpServerIntegrationTest.{check, testCaseGen}

import cats.effect.{IO, Ref}
import cats.implicits.*
import io.circe.Encoder
import munit.Assertions.assertEquals
import munit.{CatsEffectSuite, ScalaCheckEffectSuite}
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder
import org.http4s.circe.jsonEncoderOf
import org.http4s.implicits.*
import org.http4s.{EntityDecoder, HttpApp, Method, Request, Response, Status, Uri}
import org.scalacheck.Gen
import org.scalacheck.effect.PropF.forAllF

final class HttpServerIntegrationTest
    extends CatsEffectSuite
    with ScalaCheckEffectSuite
    with EmailMessageJson
    with EventIdJson
    with SmsMessageJson
    with WebhookMessageJson:

  test("it should send a message") {
    def requestFrom[A <: Message](uri: String, message: A)(implicit ev: Encoder[A]) = Request(
      method = Method.POST,
      uri = Uri.unsafeFromString(s"api/v1/$uri"),
      body = jsonEncoderOf[IO, A].toEntity(message).body,
    )

    forAllF(testCaseGen) { testCase =>
      for
        messageSenderStateRef <- Ref.of[IO, FakeMessageSenderState](
          FakeMessageSenderState.empty.setEventIds(List(testCase.eventId)),
        )
//        _ <- {
//          testCase.message match
//            case smsMessage: SmsMessage =>
//              import io.circe.syntax.*
//              IO.blocking(println(smsMessage.asJson))
//            case _ => IO.unit
//        }
        _ <- check(
          HttpServer(FakeMessageSender(messageSenderStateRef)).httpApp,
          testCase.message match
            case emailMessage: EmailMessage => requestFrom[EmailMessage]("email", emailMessage)
            case smsMessage: SmsMessage => requestFrom[SmsMessage]("sms", smsMessage)
            case webhookMessage: WebhookMessage =>
              requestFrom[WebhookMessage]("webhook", webhookMessage)
          ,
          Status.Created,
          Some(testCase.eventId),
        )
      yield ()
    }
  }

object HttpServerIntegrationTest:
  final private case class TestCase(message: Message, eventId: EventId)

  private val testCaseGen: Gen[TestCase] = for
    message <- messageGen
    eventId <- eventIdGen
  yield TestCase(message, eventId)

  def check[A](
      httpApp: HttpApp[IO],
      request: Request[IO],
      expectedStatus: Status,
      expectedBody: Option[A],
  )(implicit ev: EntityDecoder[IO, A]): IO[Unit] = for
    response <- httpApp.run(request)
    body <- expectedBody.map(_ => response.as[A]).traverse(identity)
  yield
    assertEquals(response.status, expectedStatus)
    assertEquals(body, expectedBody)
