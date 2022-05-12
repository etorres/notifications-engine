

```commandline
curl --request POST 
     --url http://localhost:8080/api/v1/sms
     --header 'content-type: application/json'
     --data '{"body":"Hi!","from":{"user":"John"},"to":{"user":"Jane"}}' 
```

```commandline
jenv exec sbt -v -Dfile.encoding=UTF-8 +check +test
```

```commandline
jenv exec sbt "project notifications-gateway" Universal/packageBin
```
