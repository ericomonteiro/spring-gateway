package ericomonteiro.github.com.gateway.filter

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import ericomonteiro.github.com.gateway.service.CryptoService
import org.reactivestreams.Publisher
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyRequestBodyGatewayFilterFactory
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.util.regex.Pattern


@Component
class EncryptRequestGatewayFilterFactory(
    private val modifyRequestBodyGatewayFilterFactory: ModifyRequestBodyGatewayFilterFactory,
    private val cryptoService: CryptoService
) : AbstractGatewayFilterFactory<Any>() {

    override fun apply(config: Any): GatewayFilter =
        modifyRequestBodyGatewayFilterFactory.apply { c -> c.setRewriteFunction(
                JsonNode::class.java,
                JsonNode::class.java,
                EncryptJson(cryptoService))
            }


    class EncryptJson(private val cryptoService: CryptoService) : RewriteFunction<JsonNode, JsonNode> {
        override fun apply(t: ServerWebExchange, u: JsonNode): Publisher<JsonNode> {
            return Mono.just(cryptoService.encryptJson(u))
        }
    }


}