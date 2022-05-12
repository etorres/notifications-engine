package es.eriktorr.notification_engine
package infrastructure

import domain.MessageDispatcher

import cats.effect.IO
import org.typelevel.log4cats.Logger

final class LoggingMessageDispatcher(logger: Logger[IO]) extends MessageDispatcher:
  override def dispatch(message: Message): IO[Unit] =
    logger.info(s"dispatch => ${message.toString}")
