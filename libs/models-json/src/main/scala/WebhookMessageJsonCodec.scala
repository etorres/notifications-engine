package es.eriktorr.notification_engine

import Message.WebhookMessage

import com.comcast.ip4s.{Host, Port}
import io.circe.*
import io.circe.generic.semiauto.*

import java.net.URL

trait WebhookMessageJsonCodec
    extends HostJsonCodec
    with PayloadJsonCodec
    with PortJsonCodec
    with UrlJsonCodec:
  implicit val webhookMessageJsonDecoder: Decoder[WebhookMessage] = (cursor: HCursor) =>
    for
      payload <- cursor.downField("payload").as[Payload]
      host <- cursor.downField("host").as[Host]
      port <- cursor.downField("port").as[Port]
      hookUrl <- cursor.downField("hookUrl").as[URL]
    yield WebhookMessage(payload, host, port, hookUrl)

  implicit val webhookMessageJsonEncoder: Encoder[WebhookMessage] = deriveEncoder[WebhookMessage]
