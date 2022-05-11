package es.eriktorr.notification_engine

import StringFieldDecoderUnitTest.{TestEntity, TestEntityError}

import io.circe
import io.circe.parser.parse
import io.circe.{DecodingFailure, Error}
import munit.FunSuite

import scala.util.control.NoStackTrace

final class StringFieldDecoderUnitTest extends FunSuite with StringFieldDecoder:

  test("should decode nested values") {
    assertEquals(
      for
        json <- parse("{\"testEntity\":\"ok\"}")
        result <- json.as[TestEntity](decode[TestEntity]("testEntity", TestEntity.from))
      yield result,
      TestEntity.right("ok"),
    )
  }

  test("should decode simple values") {
    assertEquals(
      for
        json <- parse("\"ok\"")
        result <- json.as[TestEntity](decodeValue[TestEntity](TestEntity.from))
      yield result,
      TestEntity.right("ok"),
    )
  }

  test("should fail with decoding failure when parsing illegal values") {
    assertEquals(
      for
        json <- parse("\"wrong\"")
        result <- json.as[TestEntity](decodeValue[TestEntity](TestEntity.from))
      yield result,
      Left(DecodingFailure.fromThrowable(TestEntityError(), List.empty)),
    )
  }

object StringFieldDecoderUnitTest:
  final case class TestEntityError() extends NoStackTrace

  final case class TestEntity(value: String)

  object TestEntity:
    def from(value: String): Either[TestEntityError, TestEntity] = value match
      case "ok" => Right(TestEntity(value))
      case _ => Left(TestEntityError())

    def right(value: String): Either[Error, TestEntity] = Right(new TestEntity(value))
