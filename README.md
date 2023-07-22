# Spring Gateway Demo Project

The main goal of this project is to create a way to encrypt and decrypt data using as ingress and egress

### Endpoint demo to encrypt data before to send to destiny
```shell
curl --location 'http://localhost:8080/receive/encrypted' \
--header 'Content-Type: application/json' \
--data '{
    "password": 1234
}'
```

### Endpoint demo to decrypt data before to send to the client application
```shell
curl --location 'http://localhost:8080/response/decrypted'
```

### Stacks used
- [Kotlin](https://kotlinlang.org/)
- [Spring Boot 3](https://docs.spring.io/spring-boot/docs/3.1.2/reference/html/)
- [Spring Gateway](https://docs.spring.io/spring-cloud-gateway/docs/4.0.6/reference/html/)