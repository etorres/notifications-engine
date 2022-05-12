package es.eriktorr.notification_engine
package domain

import cats.effect.IO

trait MessageSender:
  def send(message: Message): IO[EventId]
