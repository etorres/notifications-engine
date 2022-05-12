package es.eriktorr.notification_engine
package domain

import Message.{EmailMessage, SmsMessage}

import cats.effect.IO

trait MessageSender:
  def send(emailMessage: EmailMessage): IO[EventId]
  def send(smsMessage: SmsMessage): IO[EventId]
