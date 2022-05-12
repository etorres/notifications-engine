package es.eriktorr.notification_engine
package integration

import Generators.{eventIdGen, messageBodyGen, userGen}
import User.{Addressee, Sender}
import infrastructure.FakeMessageSender.FakeMessageSenderState
import infrastructure.{FakeMessageSender, HttpServer}
import integration.HttpServerIntegrationTest.{check, testCaseGen}

import cats.effect.{IO, Ref}
import cats.implicits.*
import munit.Assertions.assertEquals
import munit.{CatsEffectSuite, ScalaCheckEffectSuite}
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder
import org.http4s.circe.jsonEncoderOf
import org.http4s.implicits.*
import org.http4s.{EntityDecoder, HttpApp, Method, Request, Response, Status}
import org.scalacheck.Gen
import org.scalacheck.effect.PropF.forAllF

final class HttpServerIntegrationTest
    extends CatsEffectSuite
    with ScalaCheckEffectSuite
    with EventIdJson
    with SmsMessageJson:

  test("it should send an SMS message") {
    forAllF(testCaseGen) { testCase =>
      for
        messageSenderStateRef <- Ref.of[IO, FakeMessageSenderState](
          FakeMessageSenderState.empty.setEventIds(List(testCase.eventId)),
        )
        _ <- check(
          HttpServer(FakeMessageSender(messageSenderStateRef)).httpApp,
          Request(
            method = Method.POST,
            uri = uri"api/v1/sms",
            body = jsonEncoderOf[IO, SmsMessage].toEntity(testCase.smsMessage).body,
          ),
          Status.Created,
          Some(testCase.eventId),
        )
      yield ()
    }
  }

object HttpServerIntegrationTest:
  final private case class TestCase(smsMessage: SmsMessage, eventId: EventId)

  private val testCaseGen: Gen[TestCase] =
    for
      body <- messageBodyGen
      from <- userGen[Sender]
      to <- userGen[Addressee]
      eventId <- eventIdGen
    yield TestCase(SmsMessage(body, from, to), eventId)

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
