package es.eriktorr.notifications_engine
package domain

import cats.effect.IO

trait EventPublisher:
  def publish(event: Event): IO[Unit]
