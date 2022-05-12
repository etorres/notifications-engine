package es.eriktorr.notification_engine
package domain

import cats.effect.IO
import fs2.Stream

trait EventHandler:
  def handle: Stream[IO, Unit]
