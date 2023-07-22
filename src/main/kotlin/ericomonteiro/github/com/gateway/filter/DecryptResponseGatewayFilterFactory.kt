package ericomonteiro.github.com.gateway.filter

import com.fasterxml.jackson.databind.JsonNode
import ericomonteiro.github.com.gateway.service.CryptoService
import org.reactivestreams.Publisher
import org.springframework.cloud.gateway.filter.GatewayFilter
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory
import org.springframework.cloud.gateway.filter.factory.rewrite.ModifyResponseBodyGatewayFilterFactory
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono


@Component
class DecryptResponseGatewayFilterFactory(
    private val modifyResponseBodyGatewayFilterFactory: ModifyResponseBodyGatewayFilterFactory,
    private val cryptoService: CryptoService
) : AbstractGatewayFilterFactory<Any>() {

    override fun apply(config: Any): GatewayFilter =
        modifyResponseBodyGatewayFilterFactory.apply { c -> c.setRewriteFunction(
                JsonNode::class.java,
                JsonNode::class.java,
                DecryptJson(cryptoService))
            }


    class DecryptJson(private val cryptoService: CryptoService) : RewriteFunction<JsonNode, JsonNode> {
        override fun apply(t: ServerWebExchange, u: JsonNode): Publisher<JsonNode> {
            return Mono.just(cryptoService.decryptJson(u))
        }
    }


}