package es.eriktorr.notification_engine

import infrastructure.HttpServer

import cats.effect.{ExitCode, IO, IOApp}
import org.typelevel.log4cats.slf4j.Slf4jLogger

object NotificationsGatewayApp extends IOApp:
  private[this] def program(config: NotificationsGatewayConfig) =
    NotificationsGatewayResources.impl.use { case NotificationsGatewayResources(messageSender) =>
      HttpServer.runWith(messageSender, config.httpServerConfig)
    }

  override def run(args: List[String]): IO[ExitCode] = for
    logger <- Slf4jLogger.create[IO]
    config <- NotificationsGatewayConfig.load
    _ <- logger.info(s"Running with configuration: ${config.asString}")
    _ <- program(config)
    _ <- logger.info("Terminated!")
  yield ExitCode.Success
