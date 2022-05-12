package es.eriktorr.notification_engine

import NotificationsGatewayConfig.{HttpServerConfig, KafkaConfig}

import cats.data.NonEmptyList
import cats.effect.IO
import cats.implicits.*
import ciris.*
import com.comcast.ip4s.*

final case class NotificationsGatewayConfig(
    httpServerConfig: HttpServerConfig,
    kafkaConfig: KafkaConfig,
):
  def asString: String =
    import scala.language.unsafeNulls
    s"""http-host=${httpServerConfig.host}, 
       |http-port=${httpServerConfig.port}, 
       |bootstrap-servers=${kafkaConfig.bootstrapServers.toList.mkString(",")}, 
       |consumer-group=${kafkaConfig.consumerGroup}, 
       |topic=${kafkaConfig.consumerGroup}, 
       |schema-registry=${kafkaConfig.schemaRegistry}""".stripMargin.replaceAll("\\R", "")

object NotificationsGatewayConfig:
  opaque type NonEmptyString = String

  object NonEmptyString:
    private[this] def unsafeFrom(value: String): NonEmptyString = value

    def from(value: String): Option[NonEmptyString] =
      if value.nonEmpty then Some(unsafeFrom(value)) else Option.empty[NonEmptyString]

    extension (nonEmptyString: NonEmptyString) def value: String = nonEmptyString

  final case class HttpServerConfig(host: Host, port: Port)

  final case class KafkaConfig(
      bootstrapServers: NonEmptyList[NonEmptyString],
      consumerGroup: NonEmptyString,
      topic: NonEmptyString,
      schemaRegistry: NonEmptyString,
  ):
    def bootstrapServersAsString: String = bootstrapServers.toList.mkString(",")

  implicit def hostDecoder: ConfigDecoder[String, Host] =
    ConfigDecoder.lift(host =>
      Host.fromString(host) match
        case Some(value) => Right(value)
        case None => Left(ConfigError("Invalid host")),
    )

  implicit def portDecoder: ConfigDecoder[String, Port] = ConfigDecoder.lift(port =>
    Port.fromString(port) match
      case Some(value) => Right(value)
      case None => Left(ConfigError("Invalid port")),
  )

  implicit def nonEmptyListDecoder[A](implicit
      evA: ConfigDecoder[String, A],
  ): ConfigDecoder[String, NonEmptyList[A]] =
    import scala.language.unsafeNulls
    ConfigDecoder.lift(xs =>
      NonEmptyList
        .fromListUnsafe(xs.split(",").map(_.trim).toList)
        .traverse(evA.decode(None, _)),
    )

  private[this] val notificationsGatewayConfig = (
    env("HTTP_HOST").as[Host].option,
    env("HTTP_PORT").as[Port].option,
    env("KAFKA_BOOTSTRAP_SERVERS").as[NonEmptyList[NonEmptyString]].option,
    env("KAFKA_CONSUMER_GROUP").as[NonEmptyString].option,
    env("KAFKA_TOPIC").as[NonEmptyString].option,
    env("KAFKA_SCHEMA_REGISTRY").as[NonEmptyString].option,
  ).parMapN {
    (
        httpHost,
        httpPort,
        kafkaBootstrapServers,
        kafkaConsumerGroup,
        kafkaTopic,
        kafkaSchemaRegistry,
    ) =>
      NotificationsGatewayConfig(
        HttpServerConfig(httpHost.getOrElse(host"0.0.0.0"), httpPort.getOrElse(port"8080")),
        KafkaConfig(
          kafkaBootstrapServers.getOrElse(NonEmptyList.one("localhost:29092")),
          kafkaConsumerGroup.getOrElse("notifications-gateway"),
          kafkaTopic.getOrElse("notifications-engine-tests"),
          kafkaSchemaRegistry.getOrElse("http://localhost:8081/api/ccompat"),
        ),
      )
  }

  def load: IO[NotificationsGatewayConfig] = notificationsGatewayConfig.load
