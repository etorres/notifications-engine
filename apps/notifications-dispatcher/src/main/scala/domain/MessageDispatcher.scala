package es.eriktorr.notification_engine
package domain

import cats.effect.IO

trait MessageDispatcher:
  def dispatch[A <: Message](message: A): IO[Unit]
