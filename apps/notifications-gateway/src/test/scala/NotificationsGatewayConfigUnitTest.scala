package es.eriktorr.notifications_engine

import NotificationsGatewayConfig.HttpServerConfig
import config.KafkaConfig

import munit.CatsEffectSuite

final class NotificationsGatewayConfigUnitTest extends CatsEffectSuite:

  override def munitIgnore: Boolean = sys.env.contains("NOTIFICATIONS_ENGINE_CONFIG_OVERRIDDEN")

  test("it should load configuration properties") {
    NotificationsGatewayConfig.load.map(
      assertEquals(_, NotificationsGatewayConfig(HttpServerConfig.default, KafkaConfig.default)),
    )
  }
