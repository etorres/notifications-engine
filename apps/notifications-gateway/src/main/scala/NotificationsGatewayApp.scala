package es.eriktorr.notification_engine

import domain.MessageSender
import infrastructure.{HttpServer, KafkaEventPublisher}

import cats.effect.{ExitCode, IO, IOApp}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object NotificationsGatewayApp extends IOApp:
  private[this] def program(config: NotificationsGatewayConfig, logger: Logger[IO]) =
    NotificationsGatewayResources.impl(config).use {
      case NotificationsGatewayResources(kafkaProducer) =>
        val eventPublisher =
          KafkaEventPublisher(kafkaProducer, config.kafkaConfig.topic.value, logger)
        val messageSender = MessageSender.impl(eventPublisher)
        logger.info(s"Started HTTP server") *> HttpServer
          .runWith(messageSender, config.httpServerConfig)
    }

  override def run(args: List[String]): IO[ExitCode] = for
    logger <- Slf4jLogger.create[IO]
    config <- NotificationsGatewayConfig.load
    _ <- logger.info(s"Running with configuration: ${config.asString}")
    _ <- program(config, logger)
    _ <- logger.info("Terminated!")
  yield ExitCode.Success
