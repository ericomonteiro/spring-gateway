package ericomonteiro.github.com.gateway.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.node.ArrayNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.databind.node.TextNode
import org.reactivestreams.Publisher
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction
import org.springframework.stereotype.Service
import org.springframework.web.server.ServerWebExchange
import java.util.UUID
import java.util.regex.Pattern

@Service
class CryptoService(
    private val globalFields: Pattern = Pattern.compile("password|cvv")
) : RewriteFunction<JsonNode, JsonNode> {

    val logger: Logger = LoggerFactory.getLogger(CryptoService::class.java)

    fun encryptJson(planText: JsonNode): JsonNode {
        val (preFormatted, toEncrypt) = cryptoFormatter(planText)
        val encrypted = encrypt(toEncrypt)

        var result = preFormatted.toString()
        encrypted.forEach { entry ->
            result = result.replace(entry.key.toString(), entry.value)
        }

        return ObjectMapper().readTree(result)
    }

    private fun encrypt(planText: Map<UUID, Any>): Map<UUID, String> =
        planText
            .map { it.key to encrypt(it.value.toString()) }
            .toMap()

    private fun encrypt(planText: String): String {
        return "*** encrypted value ***"
    }

    fun decryptJson(planText: JsonNode): JsonNode {
        val (preFormatted, toEncrypt) = cryptoFormatter(planText)
        val decrypted = decrypt(toEncrypt)

        var result = preFormatted.toString()
        decrypted.forEach { entry ->
            result = result.replace(entry.key.toString(), entry.value)
        }

        return ObjectMapper().readTree(result)
    }

    private fun decrypt(planText: Map<UUID, Any>): Map<UUID, String> =
        planText
            .map { it.key to decrypt(it.value.toString()) }
            .toMap()

    private fun decrypt(encrypted: String): String {
        return "*** plan text ***"
    }

    private fun cryptoFormatter(u: JsonNode): Pair<JsonNode, Map<UUID, Any>> {
        val originalValues = mutableMapOf<UUID, Any>()
        val preFormattedJson = cryptoFormatterRecursively(u, originalValues)

        originalValues.forEach {
            logger.info(it.toString())
        }

        return Pair(preFormattedJson, originalValues)
    }

    private fun cryptoFormatterRecursively(u: JsonNode, originalValues: MutableMap<UUID, Any>): JsonNode {
        if (!u.isContainerNode) {
            return u
        }
        if (u.isObject) {
            val node = u as ObjectNode
            node.fields().forEachRemaining { f: MutableMap.MutableEntry<String, JsonNode> ->
                if (globalFields.matcher(f.key).matches() && (f.value.isTextual || f.value.isNumber)) {
                    val fieldMappedId = UUID.randomUUID()
                    originalValues[fieldMappedId] = f.value
                    f.setValue(TextNode.valueOf(fieldMappedId.toString()))
                } else {
                    f.setValue(cryptoFormatterRecursively(f.value, originalValues))
                }
            }
        } else if (u.isArray) {
            val array = u as ArrayNode
            for (i in 0 until array.size()) {
                array[i] = cryptoFormatterRecursively(array[i], originalValues)
            }
        }
        return u
    }

    override fun apply(t: ServerWebExchange?, u: JsonNode?): Publisher<JsonNode> {
        TODO("Not yet implemented")
    }

}