package es.eriktorr.notification_engine

import config.KafkaConfig
import config.KafkaConfig.{BootstrapServer, ConsumerGroup, SchemaRegistry, Topic}

import cats.data.NonEmptyList
import cats.effect.IO
import cats.implicits.*
import ciris.env
import com.comcast.ip4s.{Host, Port}

final case class NotificationsDispatcherConfig(kafkaConfig: KafkaConfig):
  def asString: String =
    import scala.language.unsafeNulls
    s"""bootstrap-servers=${kafkaConfig.bootstrapServers.toList.mkString(",")}, 
       |consumer-group=${kafkaConfig.consumerGroup}, 
       |topic=${kafkaConfig.topic}, 
       |schema-registry=${kafkaConfig.schemaRegistry}""".stripMargin.replaceAll("\\R", "")

object NotificationsDispatcherConfig extends KafkaConfigConfigDecoder:
  private[this] val notificationsDispatcherConfig = (
    env("KAFKA_BOOTSTRAP_SERVERS").as[NonEmptyList[BootstrapServer]].option,
    env("KAFKA_CONSUMER_GROUP").as[ConsumerGroup].option,
    env("KAFKA_TOPIC").as[Topic].option,
    env("KAFKA_SCHEMA_REGISTRY").as[SchemaRegistry].option,
  ).parMapN {
    (
        kafkaBootstrapServers,
        kafkaConsumerGroup,
        kafkaTopic,
        kafkaSchemaRegistry,
    ) =>
      NotificationsDispatcherConfig(
        KafkaConfig(
          kafkaBootstrapServers.getOrElse(BootstrapServer.default),
          kafkaConsumerGroup.getOrElse(ConsumerGroup.default),
          kafkaTopic.getOrElse(Topic.default),
          kafkaSchemaRegistry.getOrElse(SchemaRegistry.default),
        ),
      )
  }

  def load: IO[NotificationsDispatcherConfig] = notificationsDispatcherConfig.load
