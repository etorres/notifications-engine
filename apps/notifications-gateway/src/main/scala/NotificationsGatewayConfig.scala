package es.eriktorr.notifications_engine

import NotificationsGatewayConfig.HttpServerConfig
import config.KafkaConfig
import config.KafkaConfig.{BootstrapServer, ConsumerGroup, SchemaRegistry, Topic}

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
       |topic=${kafkaConfig.topic}, 
       |schema-registry=${kafkaConfig.schemaRegistry}""".stripMargin.replaceAll("\\R", "")

object NotificationsGatewayConfig extends KafkaConfigConfigDecoder:
  final case class HttpServerConfig(host: Host, port: Port)

  object HttpServerConfig:
    val defaultHost = host"0.0.0.0"
    val defaultPort = port"8080"

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

  private[this] val notificationsGatewayConfig = (
    env("HTTP_HOST").as[Host].option,
    env("HTTP_PORT").as[Port].option,
    env("KAFKA_BOOTSTRAP_SERVERS").as[NonEmptyList[BootstrapServer]].option,
    env("KAFKA_CONSUMER_GROUP").as[ConsumerGroup].option,
    env("KAFKA_TOPIC").as[Topic].option,
    env("KAFKA_SCHEMA_REGISTRY").as[SchemaRegistry].option,
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
        HttpServerConfig(
          httpHost.getOrElse(HttpServerConfig.defaultHost),
          httpPort.getOrElse(HttpServerConfig.defaultPort),
        ),
        KafkaConfig(
          kafkaBootstrapServers.getOrElse(BootstrapServer.default),
          kafkaConsumerGroup.getOrElse(ConsumerGroup.default),
          kafkaTopic.getOrElse(Topic.default),
          kafkaSchemaRegistry.getOrElse(SchemaRegistry.default),
        ),
      )
  }

  def load: IO[NotificationsGatewayConfig] = notificationsGatewayConfig.load
