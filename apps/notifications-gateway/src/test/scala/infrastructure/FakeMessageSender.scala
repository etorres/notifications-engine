package es.eriktorr.notification_engine
package infrastructure

import domain.MessageSender

import cats.effect.IO

final class FakeMessageSender extends MessageSender:
  override def send(smsMessage: SmsMessage): IO[EventId] = IO.fromEither(EventId.from("123"))
