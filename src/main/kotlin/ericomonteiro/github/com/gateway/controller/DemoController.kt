package ericomonteiro.github.com.gateway.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

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