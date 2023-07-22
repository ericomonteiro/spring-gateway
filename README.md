# Spring Gateway Demo Project

The main goal of this project it's create a way to encrypt and decrypt data using this gateway as ingress and egress


### Custom filters created
```properties
DecryptResponse: receive response from server and before to respond to client decrypt data
EncryptRequest: receive request from client and before to send to server encrypt data
```

### Configured routes demo
```text
decrypt_response: http://localhost:8080/response/decrypted -> http://localhost:8080/internal/response/decrypted
encrypt_request: http://localhost:8080//receive/encrypted -> http://localhost:8080//internal/receive/encrypted
```

```properties
spring.cloud.gateway.routes[0].id=decrypt_response
spring.cloud.gateway.routes[0].uri=http://localhost:8080
spring.cloud.gateway.routes[0].predicates[0].name=Method
spring.cloud.gateway.routes[0].predicates[0].args[method]=GET
spring.cloud.gateway.routes[0].predicates[1].name=Path
spring.cloud.gateway.routes[0].predicates[1].args[pattern]=/response/decrypted
spring.cloud.gateway.routes[0].filters[0].name=RewritePath
spring.cloud.gateway.routes[0].filters[0].args[regexp]=/response/decrypted
spring.cloud.gateway.routes[0].filters[0].args[replacement]=/internal/response/decrypted
spring.cloud.gateway.routes[0].filters[1].name=DecryptResponse

spring.cloud.gateway.routes[1].id=encrypt_request
spring.cloud.gateway.routes[1].uri=http://localhost:8080
spring.cloud.gateway.routes[1].predicates[0].name=Method
spring.cloud.gateway.routes[1].predicates[0].args[method]=POST
spring.cloud.gateway.routes[1].predicates[1].name=Path
spring.cloud.gateway.routes[1].predicates[1].args[pattern]=/receive/encrypted
spring.cloud.gateway.routes[1].filters[0].name=RewritePath
spring.cloud.gateway.routes[1].filters[0].args[regexp]=/receive/encrypted
spring.cloud.gateway.routes[1].filters[0].args[replacement]=/internal/receive/encrypted
spring.cloud.gateway.routes[1].filters[1].name=EncryptRequest
```

### Controller Demo
```Kotlin
@RestController
@RequestMapping("/internal")
class DemoController {
    @PostMapping("/receive/encrypted")
    fun receiveRequestEncrypted(@RequestBody body: Map<String, String>): Map<String, String> {
        return body
    }

    @GetMapping("/response/decrypted")
    fun passResponseDecrypted(): Map<String, String> {
        return mapOf("cvv" to "#encrypted_value")
    }

}
```

### Request demo to encrypt data before to send to receiver
```shell
curl --location 'http://localhost:8080/receive/encrypted' \
--header 'Content-Type: application/json' \
--data '{
    "password": "1234567",
    "cvv": "987"
}'
```
Body received in controller
```json
{
    "password": "*** encrypted value ***",
    "cvv": "*** encrypted value ***"
}
```

### Request demo to decrypt data before to send to the client application
```shell
curl --location 'http://localhost:8080/response/decrypted'
```
Original controller response
```json
{
  "cvv": "#encrypted_value"
}
```
Filtered response to client
```json
{
  "cvv": "*** plan text ***"
}
```

### Stacks used
- [Kotlin](https://kotlinlang.org/)
- [Spring Boot 3](https://docs.spring.io/spring-boot/docs/3.1.2/reference/html/)
- [Spring Gateway](https://docs.spring.io/spring-cloud-gateway/docs/4.0.6/reference/html/)