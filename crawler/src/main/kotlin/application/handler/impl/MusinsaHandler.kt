package org.team_alilm.application.handler.impl

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import org.springframework.web.client.*
import domain.product.Product
import org.team_alilm.gateway.CrawlingGateway
import org.team_alilm.application.handler.PlatformHandler
import org.team_alilm.application.handler.impl.data.SoldoutCheckResponse
import util.StringContextHolder

@Component
class MusinsaHandler(
    private val objectMapper: ObjectMapper,
    private val restClient: RestClient,
    private val crawlingGateway: CrawlingGateway,
) : PlatformHandler {

    private val goodsSaleTypeKey = "goodsSaleType"
    private val saleValue = "\"SALE\""

    private val log = LoggerFactory.getLogger(this::class.java)

    override fun process(product: Product): Boolean {
        return !isSoldOut(product)
    }

    private fun isSoldOut(product: Product): Boolean {
        val productHtmlUrl = StringContextHolder.MUSINSA_PRODUCT_HTML_URL.get().format(product.number)
        val crawlingResponse = crawlProductHtml(productHtmlUrl)
        val jsonData = extractJsonFromHtml(crawlingResponse)

        return if (jsonData != null) {
            isProductAvailable(jsonData) && checkSoldOutViaApi(product)
        } else {
            checkSoldOutViaApi(product)
        }
    }

    private fun crawlProductHtml(url: String): String {
        val request = CrawlingGateway.CrawlingGatewayRequest(url)
        return crawlingGateway.htmlCrawling(request).document.html()
    }

    private fun extractJsonFromHtml(html: String): String? {
        val pattern = "window.__MSS__.product.state = "
        val startIndex = html.indexOf(pattern)
        if (startIndex == -1) return null

        val jsonStart = html.substring(startIndex + pattern.length)
        val endIndex = jsonStart.indexOf("};") + 1
        return if (endIndex > 0) jsonStart.substring(0, endIndex) else null
    }

    private fun isProductAvailable(jsonData: String): Boolean {
        val jsonObject = objectMapper.readTree(jsonData)
        val saleType = jsonObject[goodsSaleTypeKey]?.toString()
        return saleType == saleValue
    }

    private fun checkSoldOutViaApi(product: Product): Boolean {
        val apiUrl = StringContextHolder.MUSINSA_OPTION_API_URL.get().format(product.number)
        return try {
            val response = restClient.get()
                .uri(apiUrl)
                .retrieve()
                .body(SoldoutCheckResponse::class.java)
            val optionItem = response?.data?.optionItems?.filter {
                it.managedCode == product.getManagedCode()
            }?.first()

            return optionItem?.outOfStock ?: true
        } catch (e: Exception) {
            log.error("Failed to fetch product detail from Musinsa", e)
            true
        }
    }

}
