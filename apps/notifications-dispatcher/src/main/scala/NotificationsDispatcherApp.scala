package es.eriktorr.notification_engine

import infrastructure.KafkaEventHandler

import cats.effect.{ExitCode, IO, IOApp}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object NotificationsDispatcherApp extends IOApp:
  private[this] def program(config: NotificationsDispatcherConfig, logger: Logger[IO]) =
    NotificationsDispatcherResources.impl(config).use {
      case NotificationsDispatcherResources(kafkaConsumer) =>
        logger.info(s"Started Kafka event handler") *> KafkaEventHandler(
          kafkaConsumer,
          ???, /* TODO */
        ).handle.compile.drain
    }

  override def run(args: List[String]): IO[ExitCode] = for
    logger <- Slf4jLogger.create[IO]
    config <- NotificationsDispatcherConfig.load
    _ <- logger.info(s"Running with configuration: ${config.asString}")
    _ <- program(config, logger)
    _ <- logger.info("Terminated!")
  yield ExitCode.Success
