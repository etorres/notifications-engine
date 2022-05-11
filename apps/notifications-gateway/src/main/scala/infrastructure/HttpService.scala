package es.eriktorr.notification_engine
package infrastructure

import domain.MessageSender

import cats.effect.IO
import org.http4s.circe.*
import io.circe.syntax.*
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder
import org.http4s.dsl.io.*
import org.http4s.implicits.*
import org.http4s.{HttpApp, HttpRoutes}

object HttpService extends EventIdJson with SmsMessageJson:
  def notificationsService(messageSender: MessageSender): HttpApp[IO] = HttpRoutes
    .of[IO] { case request @ POST -> Root / "api" / "v1" / "sms" =>
      for
        smsMessage <- request.as[SmsMessage]
        eventId <- messageSender.send(smsMessage)
        response <- Created(eventId.asJson)
      yield response
    }
    .orNotFound
