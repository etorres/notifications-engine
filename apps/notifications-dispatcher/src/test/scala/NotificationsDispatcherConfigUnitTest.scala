package es.eriktorr.notifications_engine

import config.KafkaConfig

import munit.CatsEffectSuite

final class NotificationsDispatcherConfigUnitTest extends CatsEffectSuite:

  override def munitIgnore: Boolean = sys.env.contains("NOTIFICATIONS_ENGINE_CONFIG_OVERRIDDEN")

  test("it should load configuration properties") {
    NotificationsDispatcherConfig.load.map(
      assertEquals(_, NotificationsDispatcherConfig(KafkaConfig.default)),
    )
  }
