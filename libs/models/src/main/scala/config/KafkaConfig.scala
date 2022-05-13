package es.eriktorr.notifications_engine
package config

import config.KafkaConfig.{BootstrapServer, ConsumerGroup, SchemaRegistry, Topic}

import cats.data.NonEmptyList

final case class KafkaConfig(
    bootstrapServers: NonEmptyList[BootstrapServer],
    consumerGroup: ConsumerGroup,
    topic: Topic,
    schemaRegistry: SchemaRegistry,
):
  def bootstrapServersAsString: String = bootstrapServers.toList.mkString(",")

object KafkaConfig:
  opaque type BootstrapServer = String

  object BootstrapServer:
    private[this] def unsafeFrom(value: String): BootstrapServer = value

    def from(value: String): Option[BootstrapServer] =
      if value.nonEmpty then Some(unsafeFrom(value)) else Option.empty[BootstrapServer]

    val default: NonEmptyList[BootstrapServer] = NonEmptyList.one(unsafeFrom("localhost:29092"))

    extension (bootstrapServer: BootstrapServer) def value: String = bootstrapServer

  opaque type ConsumerGroup = String

  object ConsumerGroup:
    private[this] def unsafeFrom(value: String): ConsumerGroup = value

    def from(value: String): Option[ConsumerGroup] =
      if value.nonEmpty then Some(unsafeFrom(value)) else Option.empty[ConsumerGroup]

    val default: ConsumerGroup = unsafeFrom("notifications-gateway")

    extension (consumerGroup: ConsumerGroup) def value: String = consumerGroup

  opaque type Topic = String

  object Topic:
    private[this] def unsafeFrom(value: String): Topic = value

    def from(value: String): Option[Topic] =
      if value.nonEmpty then Some(unsafeFrom(value)) else Option.empty[Topic]

    val default: Topic = unsafeFrom("notifications-engine-tests")

    extension (topic: Topic) def value: String = topic

  opaque type SchemaRegistry = String

  object SchemaRegistry:
    private[this] def unsafeFrom(value: String): SchemaRegistry = value

    def from(value: String): Option[SchemaRegistry] =
      if value.nonEmpty then Some(unsafeFrom(value)) else Option.empty[SchemaRegistry]

    val default: SchemaRegistry = unsafeFrom("http://localhost:8081")

    extension (schemaRegistry: SchemaRegistry) def value: String = schemaRegistry

  val default: KafkaConfig = KafkaConfig(
    BootstrapServer.default,
    ConsumerGroup.default,
    Topic.default,
    SchemaRegistry.default,
  )
