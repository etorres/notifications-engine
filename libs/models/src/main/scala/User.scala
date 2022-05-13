package es.eriktorr.notifications_engine

import NotificationError.InvalidUser
import User.Role

final case class User[A <: Role] private (value: String)

object User:
  sealed trait Role
  sealed trait Addressee extends Role
  sealed trait Sender extends Role

  private[this] def unsafeFrom[A <: Role](value: String): User[A] = User(value)

  def from[A <: Role](value: String): Either[InvalidUser, User[A]] =
    if value.nonEmpty then Right(unsafeFrom[A](value))
    else Left(InvalidUser("User cannot be empty"))
