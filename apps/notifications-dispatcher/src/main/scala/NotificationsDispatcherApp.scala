package es.eriktorr.notifications_engine

import infrastructure.{KafkaClients, KafkaEventHandler, LoggingMessageDispatcher}

import cats.effect.*
import cats.effect.IO.{IOCont, Uncancelable}
import cats.effect.kernel.Outcome
import cats.effect.std.Console
import cats.implicits.*
import fs2.kafka.KafkaConsumer
import org.typelevel.log4cats.Logger
import org.typelevel.log4cats.slf4j.Slf4jLogger

import scala.concurrent.duration.*

object NotificationsDispatcherApp extends IOApp:
  private[this] def program(config: NotificationsDispatcherConfig, logger: Logger[IO]) =
    for
      stoppedDeferred <- Deferred[IO, Either[Throwable, Unit]]
      gracefulShutdownStartedRef <- Ref[IO].of(false)
      _ <- KafkaClients
        .eventConsumerUsing(config.kafkaConfig)
        .allocated
        .bracketCase { case (consumer, _) =>
          handleEventsWith(consumer, logger).attempt.flatMap { result =>
            gracefulShutdownStartedRef.get.flatMap {
              case true => stoppedDeferred.complete(result)
              case false => IO.pure(result).rethrow
            }
          }
        } { case ((consumer, closeConsumer), exitCase) =>
          (exitCase match
            case Outcome.Errored(e) => Console[IO].errorln(s"Error caught: ${e.getMessage}")
            case _ =>
              for
                _ <- gracefulShutdownStartedRef.set(true)
                _ <- consumer.stopConsuming
                stopResult <- stoppedDeferred.get.timeoutTo(
                  10.seconds,
                  IO.pure(Left(new RuntimeException("Graceful shutdown timed out"))),
                )
                _ <- stopResult match
                  case Right(()) => IO.unit
                  case Left(e) => Console[IO].errorln(s"Error caught: ${e.getMessage}")
              yield ()
          ).guarantee(closeConsumer)
        }
    yield ()

  private[this] def handleEventsWith(
      consumer: KafkaConsumer[IO, String, Event],
      logger: Logger[IO],
  ) =
    val messageDispatcher = LoggingMessageDispatcher(logger)
    logger.info(s"Started Kafka event handler") *> KafkaEventHandler(
      consumer,
      messageDispatcher,
    ).handle.compile.drain

  override def run(args: List[String]): IO[ExitCode] = for
    logger <- Slf4jLogger.create[IO]
    config <- NotificationsDispatcherConfig.load
    _ <- logger.info(s"Running with configuration: ${config.asString}")
    _ <- program(config, logger)
    _ <- logger.info("Terminated!")
  yield ExitCode.Success
