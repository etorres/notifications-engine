package es.eriktorr.notifications_engine
package infrastructure

import NotificationsGatewayConfig.HttpServerConfig
import domain.MessageSender

import cats.effect.IO
import io.circe.Decoder
import io.circe.syntax.*
import org.http4s.circe.*
import org.http4s.circe.CirceEntityDecoder.circeEntityDecoder
import org.http4s.dsl.io.*
import org.http4s.ember.server.EmberServerBuilder
import org.http4s.implicits.*
import org.http4s.server.middleware.{CORS, GZip, Logger as Http4sLogger}
import org.http4s.{HttpApp, HttpRoutes, Method, Request}

import scala.concurrent.duration.DurationInt

final class HttpServer(messageSender: MessageSender) extends EventIdJsonCodec with MessageJsonCodec:
  val httpApp: HttpApp[IO] = HttpRoutes
    .of[IO] {
      case request @ POST -> Root / "api" / "v1" / "email" => send(request)
      case request @ POST -> Root / "api" / "v1" / "sms" => send(request)
      case request @ POST -> Root / "api" / "v1" / "webhook" => send(request)
    }
    .orNotFound

  private[this] def send(request: Request[IO]) = for
    message <- request.as[Message]
    eventId <- messageSender.send(message)
    response <- Created(eventId.asJson.noSpaces)
  yield response

object HttpServer:
  def runWith(messageSender: MessageSender, httpServerConfig: HttpServerConfig): IO[Unit] =
    val httpServer = HttpServer(messageSender)

    val enhancedHttpApp = Http4sLogger.httpApp(logHeaders = true, logBody = true)(
      CORS.policy.withAllowOriginAll
        .withAllowMethodsIn(Set(Method.GET, Method.POST))
        .withAllowCredentials(false)
        .withMaxAge(1.day)
        .apply(GZip(httpServer.httpApp)),
    )

    EmberServerBuilder
      .default[IO]
      .withHost(httpServerConfig.host)
      .withPort(httpServerConfig.port)
      .withHttpApp(enhancedHttpApp)
      .build
      .use(_ => IO.never)
