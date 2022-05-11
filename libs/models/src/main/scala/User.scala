package es.eriktorr.notification_engine

import NotificationError.InvalidUser
import User.Role

import org.tpolecat.typename.{typeName, TypeName}

final case class User[A <: Role] private (value: String)

object User:
  sealed trait Role
  sealed trait Addressee extends Role
  sealed trait Sender extends Role

  private[this] def unsafeFrom[A <: Role](value: String): User[A] = User(value)

  def from[A <: Role](value: String)(implicit ev: TypeName[A]): Either[InvalidUser, User[A]] =
    ev.value match
      case t if t == typeName[Addressee] || t == typeName[Sender] =>
        if value.nonEmpty then Right(unsafeFrom[A](value))
        else Left(InvalidUser("User cannot be empty"))
      case _ => Left(InvalidUser("Unknown role"))
