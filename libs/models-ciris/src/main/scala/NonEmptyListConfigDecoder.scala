package es.eriktorr.notification_engine

import cats.data.NonEmptyList
import ciris.{ConfigDecoder, ConfigError}

trait NonEmptyListConfigDecoder:
  implicit def nonEmptyListConfigDecoder[A](implicit
      evA: ConfigDecoder[String, A],
  ): ConfigDecoder[String, NonEmptyList[A]] =
    import scala.language.unsafeNulls
    ConfigDecoder.lift(xs =>
      NonEmptyList
        .fromListUnsafe(xs.split(",").map(_.trim).toList)
        .traverse(evA.decode(None, _)),
    )
