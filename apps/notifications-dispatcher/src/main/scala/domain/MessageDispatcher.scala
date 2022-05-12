package es.eriktorr.notification_engine
package domain

import cats.effect.IO

trait MessageDispatcher:
  def dispatch(message: Message): IO[Unit]
