package es.eriktorr.notifications_engine

import infrastructure.{KafkaClients, KafkaEventHandler, LoggingMessageDispatcher}

import cats.effect.{ExitCode, IO, IOApp}
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

object NotificationsDispatcherApp extends IOApp:
  private[this] def program(config: NotificationsDispatcherConfig, logger: Logger[IO]) =
    KafkaClients.eventConsumerUsing(config.kafkaConfig).use { kafkaConsumer =>
      val messageDispatcher = LoggingMessageDispatcher(logger)
      logger.info(s"Started Kafka event handler") *> KafkaEventHandler(
        kafkaConsumer,
        messageDispatcher,
      ).handle.compile.drain
    }

  override def run(args: List[String]): IO[ExitCode] = for
    logger <- Slf4jLogger.create[IO]
    config <- NotificationsDispatcherConfig.load
    _ <- logger.info(s"Running with configuration: ${config.asString}")
    _ <- program(config, logger)
    _ <- logger.info("Terminated!")
  yield ExitCode.Success
