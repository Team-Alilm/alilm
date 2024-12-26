package org.team_alilm.application.service

import com.fasterxml.jackson.databind.JsonNode
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.springframework.web.client.RestTemplate
import org.team_alilm.application.port.`in`.use_case.product.crawling.ProductCrawlingUseCase
import org.team_alilm.domain.product.Store
import org.team_alilm.global.util.StringConstant

@Service
class AblyProductCrawlingService(
    private val restClient: RestClient,
) : ProductCrawlingUseCase {

    private val log = org.slf4j.LoggerFactory.getLogger(javaClass)

    override fun crawling(command: ProductCrawlingUseCase.ProductCrawlingCommand): ProductCrawlingUseCase.CrawlingResult {
        val productNumber = getProductNumber(command.url)
        val headers = HttpHeaders().apply {
            add("X-Anonymous-Token", StringConstant.ABLY_ANONYMOUS_TOKEN.get()) // Authorization 헤더
        }
        val entity = HttpEntity<String>(headers)

        val response = restClient.get()
            .uri(StringConstant.ABLY_PRODUCT_API_URL.get().format(productNumber))
            .headers(
                HttpHeaders().apply {
                    add("X-Anonymous-Token", StringConstant.ABLY_ANONYMOUS_TOKEN.get())
                }
            )
            .retrieve()
            JsonNode::class.java  // 응답 타입
        ).body

        val firstOptions = restTemplate.exchange(
            StringConstant.ABLY_PRODUCT_OPTIONS_API_URL.get().format(productNumber, 1),
            HttpMethod.GET,
            entity,
            JsonNode::class.java
        ).body

        val secondOptions = try {
            if (firstOptions?.get("option_components")?.first()?.isEmpty?.not() == true) {
                restTemplate.exchange(
                    StringConstant.ABLY_PRODUCT_OPTIONS_API_URL.get().format(productNumber, 2) + "&selected_option_sno=${
                        firstOptions.get("option_components")
                            ?.first()?.get("goods_option_sno")
                    }",
                    HttpMethod.GET,
                    entity,
                    JsonNode::class.java
                ).body
            } else {
                null
            }
        } catch (e: Exception) {
            log.error("Error while fetching second options: ${e.message}")
            null
        }

        val thirdOptions = try {
            if (secondOptions?.get("option_components")?.first()?.isEmpty?.not() == true) {
                restTemplate.exchange(
                    StringConstant.ABLY_PRODUCT_OPTIONS_API_URL.get().format(productNumber, 3) + "&selected_option_sno=${
                        firstOptions?.get("option_components")
                            ?.first()?.get("goods_option_sno")
                    }",
                    HttpMethod.GET,
                    entity,
                    JsonNode::class.java
                ).body
            } else {
                null
            }
        } catch (e: Exception) {
            log.error("""
                |Error while fetching third options: ${e.message}
                |https://api.a-bly.com/api/v2/goods/$productNumber/options/?depth=1
            """.trimMargin())
            null
        }

        val relatedGoodsApiRequestUrl = StringConstant.ABLY_PRODUCT_RELATED_GOODS_API_URL.get().format(productNumber)
        val relatedGoodsResponse = restTemplate.exchange(
            relatedGoodsApiRequestUrl,
            HttpMethod.GET,
            entity,
            JsonNode::class.java
        ).body

        val imageUrlList = relatedGoodsResponse
            ?.get("item_list")
            ?.first { it.get("type")?.asText() == "image_similar_goods" }
            ?.get("goods")
            ?.map { it.get("image").asText() }
            ?.take(3) // 상위 3개만 추출
            ?: emptyList()

        return ProductCrawlingUseCase.CrawlingResult(
            number = response?.get("goods")?.get("sno")?.asLong() ?: throw IllegalArgumentException("상품 정보를 가져올 수 없습니다."),
            name = response.get("goods")?.get("name")?.asText() ?: throw IllegalArgumentException("상품 정보를 가져올 수 없습니다."),
            brand = response.get("goods")?.get("market")?.get("name")?.asText() ?: throw IllegalArgumentException("상품 정보를 가져올 수 없습니다."),
            thumbnailUrl = response.get("goods")?.get("first_page_rendering")?.get("cover_image")?.asText() ?: throw IllegalArgumentException("상품 정보를 가져올 수 없습니다."),
            imageUrlList = imageUrlList,
            firstCategory = response.get("goods")?.get("category")?.get("name")?.asText() ?: throw IllegalArgumentException("상품 정보를 가져올 수 없습니다."),
            secondCategory = null,
            price = response.get("goods")?.get("first_page_rendering")?.get("original_price")?.asInt() ?: throw IllegalArgumentException("상품 정보를 가져올 수 없습니다."),
            store = Store.A_BLY,
            firstOptions = firstOptions?.get("option_components")?.map { it.get("name")?.asText() ?: "" } ?: emptyList(),
            secondOptions = secondOptions?.get("option_components")?.map { it.get("name")?.asText() ?: "" } ?: emptyList(),
            thirdOptions = thirdOptions?.get("option_components")?.map { it.get("name")?.asText() ?: "" } ?: emptyList(),
        )
    }

    private fun getProductNumber(url: String): Long {
        return url.split("/").last().toLong()
    }
}