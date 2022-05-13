package es.eriktorr.notifications_engine

import scala.util.control.NoStackTrace

@SuppressWarnings(Array("org.wartremover.warts.Null"))
sealed abstract class NotificationError(
    message: String,
    cause: Option[Throwable] = Option.empty[Throwable],
) extends NoStackTrace:
  import scala.language.unsafeNulls

  override def getCause: Throwable = cause.orNull
  override def getMessage: String = message

object NotificationError:
  final case class InvalidEventId(message: String) extends NotificationError(message)
  final case class InvalidMessageBody(message: String) extends NotificationError(message)
  final case class InvalidMessageSubject(message: String) extends NotificationError(message)
  final case class InvalidUser(message: String) extends NotificationError(message)
