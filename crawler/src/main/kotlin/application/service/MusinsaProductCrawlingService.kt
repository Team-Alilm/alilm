package org.team_alilm.application.service

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.JsonNode
import domain.product.Store
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.web.client.RestClient
import org.team_alilm.application.port.use_case.ProductCrawlingUseCase
import org.team_alilm.error.NotParsingHtml
import org.team_alilm.gateway.CrawlingGateway
import org.team_alilm.gateway.CrawlingGateway.CrawlingGatewayRequest
import util.StringContextHolder

@Service
class MusinsaProductCrawlingService(
    private val crawlingGateway: CrawlingGateway,
    private val restClient: RestClient
) : ProductCrawlingUseCase {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun crawling(command: ProductCrawlingUseCase.ProductCrawlingCommand): ProductCrawlingUseCase.CrawlingResult {
        val crawlingGatewayResponse = crawlingGateway.htmlCrawling(request = CrawlingGatewayRequest(url = command.url))
        val productHtmlResponse = extractJsonData(crawlingGatewayResponse.document.html())
            ?: throw NotParsingHtml()

        val optionUri = getOptionUri(productHtmlResponse.get("goodsNo").asLong())
        val optionResponse = restClient.get()
            .uri(optionUri)
            .retrieve()
            .body(JsonNode::class.java)

        val filterOption = optionResponse?.get("data")?.get("filterOption") ?: throw RuntimeException("Failed to get option data")

        val firstOptions = filterOption.get("firstOptions").map { it.get("val").asText() }
        val secondOptions = filterOption.get("secondOptions")?.map { it.get("val").asText() } ?: emptyList()
        val thirdOptions = filterOption.get("thirdOptions")?.map { it.get("val").asText() } ?: emptyList()

        val imageUrlListRequsetUri = StringContextHolder.MUSINSA_PRODUCT_IMAGES_URL.get().format(productHtmlResponse.get("goodsNo").asLong())
        val imageUrlListResponse = restClient.get()
            .uri(imageUrlListRequsetUri)
            .retrieve()
            .body(JsonNode::class.java)

        return ProductCrawlingUseCase.CrawlingResult(
            number = productHtmlResponse.get("goodsNo").asLong(),
            name = productHtmlResponse.get("goodsNm").asText(),
            brand = productHtmlResponse.get("brandInfo").get("brandName").asText(),
            thumbnailUrl = getThumbnailUrl(productHtmlResponse.get("thumbnailImageUrl").asText()),
            imageUrlList = imageUrlListResponse?.get("data")?.get("similar")?.get(0)?.get("recommendedGoodsList")?.map { it.get("imageUrl").asText() } ?: emptyList(),
            firstCategory = productHtmlResponse.get("category").get("categoryDepth1Name").asText(),
            secondCategory = productHtmlResponse.get("category").get("categoryDepth2Name").asText(),
            price = productHtmlResponse.get("goodsPrice").get("normalPrice").asInt(),
            store = Store.MUSINSA,
            firstOptions = firstOptions, // 추가된 부분
            secondOptions = secondOptions, // 추가된 부분
            thirdOptions = thirdOptions // 추가된 부분
        )
    }

    private fun extractJsonData(scriptContent: String): JsonNode? {
        var jsonString: String? = null

        // 자바스크립트 내 변수 선언 패턴
        val pattern = "window.__MSS__.product.state = "
        // 패턴의 시작 위치 찾기
        val startIndex = scriptContent.indexOf(pattern)

        if (startIndex != -1) {
            // 패턴 이후 부분 추출
            val substring = scriptContent.substring(startIndex + pattern.length)
            // JSON 데이터의 끝 위치 찾기
            val endIndex = substring.indexOf("};") + 1
            // JSON 문자열 추출
            jsonString = substring.substring(0, endIndex)
        }

        return jsonString?.let {
            val mapper = com.fasterxml.jackson.databind.ObjectMapper()
            mapper.readTree(it)
        }
    }

    private fun getOptionUri(goodsNo: Long): String {
        return "https://goods.musinsa.com/api2/review/v1/view/filter?goodsNo=${goodsNo}"
    }

    private fun getThumbnailUrl(thumbnailUrl: String): String {
        return "https://image.msscdn.net/thumbnails${thumbnailUrl}"
    }

    // null 허용을 고려해 보자!
    data class CrawlingGatewayRequest(
        val goodsNo: Long,
        val goodsNm: String,
        val thumbnailImageUrl: String,
        val brandInfo: BrandInfo,
        val category: Category,
        val goodsPrice: GoodsPrice
    )

    data class BrandInfo(
        val brandName: String,
    )

    data class Category(
        val categoryDepth1Name: String,
        val categoryDepth2Name: String,
    )

    data class GoodsPrice(
        val normalPrice: Int,
    )

    data class FilterOption(
        @JsonProperty("val") val optionCount: Int,
        val firstOptions: List<Option> = emptyList(),
        val secondOptions: List<Option> = emptyList(),
        val thirdOptions: List<Option> = emptyList(),
        val filterText: String
    )

    data class Option(
        @JsonProperty("val") val value: String,
        val txt: String,
        val qty: Int,
        val memo: String?,
        val title: String,
        val qnaTitle: String?,
        val restockYn: String?
    )


    data class MyProfileInfo(
        val hasMySize: Boolean,
        val myFilterEnum: String,
        val searchMemberInfo: Any?,
        val weight: Int,
        val height: Int,
        val skinInfoList: Any?,
        val filterText: String,
        val heightStart: Int,
        val heightEnd: Int,
        val weightStart: Int,
        val weightEnd: Int
    )

    data class ResponseData(
        val filterOption: FilterOption,
        val myProfileInfo: MyProfileInfo
    )

    data class Meta(
        val result: String,
        val errorCode: String?,
        val message: String?
    )

    data class ApiResponse(
        val data: ResponseData,
        val meta: Meta
    )

}