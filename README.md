# Notifications Engine

This is an adaptation of the blog entry [Building High-Performance Notification Engine Using Pure Functional Scala, ZIO HTTP, ZIO Kafka. Event-Driven Architecture](https://www.linkedin.com/pulse/building-high-performance-notification-engine-using-pure-otun/) (by [Oluwaseyi Otun](https://github.com/seyijava)) to the following stack:
* [Cats Effect](https://typelevel.org/cats-effect/).
* [FS2 Kafka](https://fd4s.github.io/fs2-kafka/).
* [MUnit](https://scalameta.org/munit/).

## Examples

```commandline
curl --request POST 
     --url http://localhost:8080/api/v1/sms
     --header 'content-type: application/json'
     --data '{"body":"Hi!","from":{"user":"John"},"to":{"user":"Jane"}}' 
```
## Build binary packages

```commandline
jenv exec sbt "project notifications-gateway" Universal/packageBin
```

```commandline
jenv exec sbt "project notifications-dispatcher" Universal/packageBin
```
