package es.eriktorr.notification_engine

import User.Role

import org.scalacheck.Gen

object Generators:
  private[this] def textGen(minLength: Int = 3, maxLength: Int = 10): Gen[String] = for
    length <- Gen.choose(minLength, maxLength)
    text <- Gen.listOfN[Char](length, Gen.alphaNumChar).map(_.mkString)
  yield text

  def eventIdGen: Gen[EventId] = Gen.uuid.map(uuid => EventId.from(uuid.toString).toOption.get)

  def messageBodyGen: Gen[MessageBody] = textGen(1, 24).map(MessageBody.from(_).toOption.get)

  def userGen[A <: Role]: Gen[User[A]] = textGen().map(User.from[A](_).toOption.get)
