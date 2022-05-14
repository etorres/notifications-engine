# Notifications Engine
[![GitHub Workflow Status](https://img.shields.io/github/workflow/status/etorres/notifications-engine/CI?logo=github&style=flat)](https://github.com/etorres/notifications-engine/actions?query=workflow%3A%22CI%22)
[![Scala Steward badge](https://img.shields.io/badge/Scala_Steward-helping-blue.svg?style=flat&logo=data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAA4AAAAQCAMAAAARSr4IAAAAVFBMVEUAAACHjojlOy5NWlrKzcYRKjGFjIbp293YycuLa3pYY2LSqql4f3pCUFTgSjNodYRmcXUsPD/NTTbjRS+2jomhgnzNc223cGvZS0HaSD0XLjbaSjElhIr+AAAAAXRSTlMAQObYZgAAAHlJREFUCNdNyosOwyAIhWHAQS1Vt7a77/3fcxxdmv0xwmckutAR1nkm4ggbyEcg/wWmlGLDAA3oL50xi6fk5ffZ3E2E3QfZDCcCN2YtbEWZt+Drc6u6rlqv7Uk0LdKqqr5rk2UCRXOk0vmQKGfc94nOJyQjouF9H/wCc9gECEYfONoAAAAASUVORK5CYII=)](https://scala-steward.org)
[![Mergify Status](https://img.shields.io/endpoint.svg?url=https://api.mergify.com/v1/badges/etorres/notifications-engine&style=flat)](https://mergify.io)

This is an adaptation of the blog entry [Building High-Performance Notification Engine Using Pure Functional Scala, ZIO HTTP, ZIO Kafka. Event-Driven Architecture](https://www.linkedin.com/pulse/building-high-performance-notification-engine-using-pure-otun/) (by [Oluwaseyi Otun](https://github.com/seyijava)) to the following stack:
* [Cats Effect](https://typelevel.org/cats-effect/): Is an asynchronous runtime for Scala.
* [FS2 Kafka](https://fd4s.github.io/fs2-kafka/): Provides Apache Kafka Streams for Scala.
* [Vulcan](https://fd4s.github.io/vulcan/): Provides Apache Avro data serialization for Scala.
* [http4s](https://http4s.org/): HTTP servers and clients for Scala.
* [MUnit](https://scalameta.org/munit/): Is a Scala testing library.

## Examples

```shell
curl --url http://localhost:8080/api/v1/email \
     --header 'content-type: application/json' \
     --data '{"body":"Hello","subject":"Greetings from Mary","from":{"user":"Mary"},"to":{"user":"Rose"}}' 
```
```shell
curl --url http://localhost:8080/api/v1/sms \
     --header 'content-type: application/json' \
     --data '{"body":"Hi!","from":{"user":"John"},"to":{"user":"Jane"}}' 
```

```shell
curl --url http://localhost:8080/api/v1/webhook \
     --header 'content-type: application/json' \
     --data '{"body":"Have a nice day!","host":"www.example.org","port":"8080","hookUrl":"http://example.org/hook"}'
```

## Build binary packages

```shell
sbt "project notifications-gateway" Universal/packageBin
```

```shell
sbt "project notifications-dispatcher" Universal/packageBin
```
## Schema

```shell
curl -s http://localhost:8081/subjects | jq
```
```json
[
  "notifications-engine-tests-key",
  "notifications-engine-tests-value"
]
```

```shell
curl -s http://localhost:8081/subjects/notifications-engine-tests-key/versions/latest | jq
```
```json
{
  "subject": "notifications-engine-tests-key",
  "version": 1,
  "id": 1,
  "schema": "\"string\""
}
```

```shell
curl -s http://localhost:8081/subjects/notifications-engine-tests-value/versions/latest | jq
```
```json
{
  "subject": "notifications-engine-tests-value",
  "version": 1,
  "id": 2,
  "schema": "[{\"type\":\"record\",\"name\":\"EmailSent\",\"namespace\":\"es.eriktorr.notifications_engine\",\"fields\":[{\"name\":\"id\",\"type\":\"string\"},{\"name\":\"emailMessage\",\"type\":{\"type\":\"record\",\"name\":\"EmailMessage\",\"fields\":[{\"name\":\"body\",\"type\":\"string\"},{\"name\":\"subject\",\"type\":\"string\"},{\"name\":\"from\",\"type\":\"string\"},{\"name\":\"to\",\"type\":\"string\"}]}}]},{\"type\":\"record\",\"name\":\"SmsSent\",\"namespace\":\"es.eriktorr.notifications_engine\",\"fields\":[{\"name\":\"id\",\"type\":\"string\"},{\"name\":\"smsMessage\",\"type\":{\"type\":\"record\",\"name\":\"SmsMessage\",\"fields\":[{\"name\":\"body\",\"type\":\"string\"},{\"name\":\"from\",\"type\":\"string\"},{\"name\":\"to\",\"type\":\"string\"}]}}]},{\"type\":\"record\",\"name\":\"WebhookSent\",\"namespace\":\"es.eriktorr.notifications_engine\",\"fields\":[{\"name\":\"id\",\"type\":\"string\"},{\"name\":\"webhookMessage\",\"type\":{\"type\":\"record\",\"name\":\"WebhookMessage\",\"fields\":[{\"name\":\"body\",\"type\":\"string\"},{\"name\":\"host\",\"type\":\"string\"},{\"name\":\"port\",\"type\":\"int\"},{\"name\":\"hookUrl\",\"type\":\"string\"}]}}]}]"
```

```shell
curl -s http://localhost:8081/subjects/notifications-engine-tests-value/versions
```
```json
[1]
```

## See also
* [Putting Several Event Types in the Same Topic – Revisited](https://www.confluent.io/blog/multiple-event-types-in-the-same-kafka-topic/).
* [Gracefully shutdown a Kafka consumer](https://fd4s.github.io/fs2-kafka/docs/consumers#graceful-shutdown).
* [MonadCancel](https://typelevel.org/cats-effect/docs/typeclasses/monadcancel).