package es.eriktorr.notification_engine
package infrastructure

import NotificationsGatewayConfig.HttpServerConfig
import domain.MessageSender

import cats.effect.IO
import io.circe.syntax.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder
import org.http4s.dsl.io.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.middleware.{CORS, GZip, Logger as Http4sLogger}
import org.http4s.{HttpApp, HttpRoutes}

final class HttpServer(messageSender: MessageSender) extends EventIdJson with SmsMessageJson:
  val httpApp: HttpApp[IO] = HttpRoutes
    .of[IO] { case request @ POST -> Root / "api" / "v1" / "sms" =>
      for
        smsMessage <- request.as[SmsMessage]
        eventId <- messageSender.send(smsMessage)
        response <- Created(eventId.asJson)
      yield response
    }
    .orNotFound

object HttpServer:
  def runWith(messageSender: MessageSender, httpServerConfig: HttpServerConfig): IO[Unit] =
    val httpServer = HttpServer(messageSender)

    val enhancedHttpApp = Http4sLogger.httpApp(logHeaders = true, logBody = true)(
      CORS.policy.withAllowOriginAll(GZip(httpServer.httpApp)),
    )

    EmberServerBuilder
      .default[IO]
      .withHost(httpServerConfig.host)
      .withPort(httpServerConfig.port)
      .withHttpApp(enhancedHttpApp)
      .build
      .use(_ => IO.never)
