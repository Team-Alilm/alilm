package org.team_alilm.application.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import domain.product.Store
import org.jsoup.nodes.Document
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.team_alilm.application.port.use_case.ProductCrawlingUseCase
import org.team_alilm.gateway.CrawlingGateway

@Service
class ZigzagProductCrawlingService(
    private val crawlingGateway: CrawlingGateway,
    private val objectMapper: ObjectMapper
) : ProductCrawlingUseCase {

    private val log = LoggerFactory.getLogger(ZigzagProductCrawlingService::class.java)

    override fun crawling(command: ProductCrawlingUseCase.ProductCrawlingCommand): ProductCrawlingUseCase.CrawlingResult {
        val response = crawlingGateway.htmlCrawling(request = CrawlingGateway.CrawlingGatewayRequest(url = command.url))
        val document = response.document
        val scriptTag = document.select("script#__NEXT_DATA__").firstOrNull()?.data() ?: ""
        val scriptJsonNode = objectMapper.readTree(scriptTag)

        val (brand, name) = getProductBrandAndName(document.title())
        val (thumbnailUrl, imageList) = getThumbnailUrlAndImageList(document)
        val price = getPrice(document)
        val (firstCategory, secondCategory) = getCategorys(scriptJsonNode)
        val (firstOptions, secondOptions, thirdOptions) = getOptions(scriptJsonNode)

        return ProductCrawlingUseCase.CrawlingResult(
            number = 0,
            name = name,
            brand = brand,
            thumbnailUrl = thumbnailUrl,
            imageUrlList = imageList,
            firstCategory = firstCategory,
            secondCategory = secondCategory,
            price = price,
            store = Store.ZIGZAG,
            firstOptions = firstOptions,
            secondOptions = secondOptions,
            thirdOptions = thirdOptions
        )
    }

    private fun getProductBrandAndName(titleTag: String): Pair<String, String> {
        val parts = titleTag.split(" ", limit = 2) // 첫 공백 기준으로 두 부분으로만 분리
        val brand = parts.getOrNull(0) ?: ""        // 첫 번째 단어 (없으면 빈 문자열)
        val name = parts.getOrNull(1) ?: ""         // 나머지 부분 (없으면 빈 문자열)

        return Pair(brand, name)
    }

    private fun getThumbnailUrlAndImageList(document: Document): Pair<String, List<String>> {
        // "상품 이미지"인 모든 <img> 태그 가져오기
        val imageElements = document.select("img[alt='상품 이미지']")

        // 첫 번째 이미지의 src
        val firstImageUrl = imageElements.firstOrNull()?.attr("src") ?: ""

        // 두 번째 이후의 이미지의 src를 List<String>으로 변환
        val otherImageUrls = imageElements
            .drop(1) // 첫 번째 이미지를 제외하고 나머지를 반환
            .map { it.attr("src") }
            .filter { it.isNotBlank() } // 빈 값 제거

        // 첫 번째 이미지와 나머지 이미지를 Pair로 반환
        return Pair(firstImageUrl, otherImageUrls)
    }

    private fun getPrice(document: Document): Int {
        // <meta property="product:price:amount"> 태그에서 content 속성 값을 추출
        val priceElement = document.select("meta[property='product:price:amount']").firstOrNull()
        val priceText = priceElement?.attr("content") ?: return 0

        // 쉼표 제거 후 Double 변환
        return try {
            priceText.replace(",", "").toInt()
        } catch (e: NumberFormatException) {
            0
        }
    }

    private fun getCategorys(jsonNode: JsonNode): Pair<String, String> {
        jsonNode.let {
            try {
                val props = it.get("props")
                val pageProps = props.get("pageProps")
                val product = pageProps.get("product")
                val managedCategoryList = product.get("managed_category_list")

                return Pair(
                    managedCategoryList[1]?.get("value")?.asText() ?: "",
                    managedCategoryList[2]?.get("value")?.asText() ?: ""
                )
            } catch (e: Exception) {
                // 예외 발생시 빈 문자열 반환
                return Pair("", "")
            }
        }
    }

    private fun getOptions(jsonNode: JsonNode): Triple<List<String>, List<String>, List<String>> {
        jsonNode.let {
            try {
                val props = it.get("props")
                val pageProps = props.get("pageProps")
                val product = pageProps.get("product")
                val options = product.get("product_item_attribute_list")

                log.info("options: ${options[0]?.get("value_list")}")

                val firstOptions =
                    options[0]?.get("value_list")?.map { value -> value.get("value").asText() } ?: emptyList()
                val secondOptions =
                    options[1]?.get("value_list")?.map { value -> value.get("value").asText() } ?: emptyList()
                val thirdOptions =
                    options[2]?.get("value_list")?.map { value -> value.get("value").asText() } ?: emptyList()

                return Triple(firstOptions, secondOptions, thirdOptions)
            } catch (e: Exception) {
                // 예외 발생시 빈 리스트 반환
                return Triple(emptyList(), emptyList(), emptyList())
            }
        }
    }
}