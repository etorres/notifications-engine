package es.eriktorr.notification_engine
package integration

import User.{Addressee, Sender}
import infrastructure.{FakeMessageSender, HttpService}
import integration.HttpServiceIntegrationTest.check

import cats.effect.IO
import cats.implicits.*
import munit.Assertions.assertEquals
import munit.CatsEffectSuite
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder
import org.http4s.circe.jsonEncoderOf
import org.http4s.implicits.*
import org.http4s.{EntityDecoder, HttpApp, Method, Request, Response, Status}

final class HttpServiceIntegrationTest extends CatsEffectSuite with EventIdJson with SmsMessageJson:

  test("should send SMS message") {
    for
      body <- IO.fromEither(MessageBody.from("Hi!"))
      from <- IO.fromEither(User.from[Sender]("Jane Doe"))
      to <- IO.fromEither(User.from[Addressee]("John Doe"))
      eventId <- IO.fromEither(EventId.from("123"))
      smsMessage = SmsMessage(body, from, to)
      _ <- check(
        HttpService.notificationsService(FakeMessageSender()),
        Request(
          method = Method.POST,
          uri = uri"api/v1/sms",
          body = jsonEncoderOf[IO, SmsMessage].toEntity(smsMessage).body,
        ),
        Status.Created,
        Some(eventId),
      )
    yield ()
  }

object HttpServiceIntegrationTest:
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
