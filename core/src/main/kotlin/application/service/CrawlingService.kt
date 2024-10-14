//package org.team_alilm.application.service
//
//import com.google.api.pathtemplate.ValidationException
//import com.google.gson.JsonParser
//import org.slf4j.LoggerFactory
//import org.springframework.stereotype.Service
//import org.springframework.transaction.annotation.Transactional
//import org.springframework.web.client.RestClient
//import org.springframework.web.client.body
//import org.team_alilm.application.port.out.gateway.CrawlingGateway.CrawlingGatewayRequest
//import org.team_alilm.domain.Product
//import org.team_alilm.global.error.MusinsaSoldoutCheckException
//import org.team_alilm.global.quartz.data.SoldoutCheckResponse
//import org.team_alilm.global.util.StringConstant
//import java.net.URI
//import java.net.URLDecoder
//import java.nio.charset.StandardCharsets
//
///**
// * 상품 크롤링 서비스
// * 현재 무신사 종속적인 코드로 구현되어 있음
// */
//@Service
//@Transactional(readOnly = true)
//class CrawlingService(
//    private val crawlingGateway: org.team_alilm.application.port.out.gateway.CrawlingGateway,
//    private val restClient: RestClient,
//) : org.team_alilm.application.port.`in`.use_case.CrawlingUseCase {
//
//    private val log = LoggerFactory.getLogger(this::class.java)
//
//    override fun productCrawling(command: org.team_alilm.application.port.`in`.use_case.CrawlingUseCase.ProductCrawlingCommand): org.team_alilm.application.port.`in`.use_case.CrawlingUseCase.CrawlingResult {
//        val decodedUrl = decodeUrl(command.url)
//        val document = crawlingGateway.crawling(CrawlingGatewayRequest(decodedUrl)).document
//        val scriptContent = document.getElementsByTag("script").html()
//        val jsonData = extractJsonData(scriptContent, "window.__MSS__.product.state")
//        val jsonObject = JsonParser.parseString(jsonData).asJsonObject
//
//        val soldoutCheckResponse = fetchSoldoutCheckResponse(jsonObject.get("goodsNo").toString())
//        val options = extractOptions(soldoutCheckResponse)
//
//        return org.team_alilm.application.port.`in`.use_case.CrawlingUseCase.CrawlingResult(
//            number = jsonObject.get("goodsNo").asLong,
//            name = jsonObject.get("goodsNm").asString,
//            brand = jsonObject.get("brand").asString,
//            imageUrl = "https://image.msscdn.net${jsonObject.get("thumbnailImageUrl").asString}",
//            category = jsonObject.get("category").asJsonObject.get("categoryDepth1Title").asString,
//            price = jsonObject.get("goodsPrice").asJsonObject.get("maxPrice").asInt,
//            store = Product.Store.MUSINSA,
//            firstOptions = options.first,
//            secondOptions = options.second,
//            thirdOptions = options.third
//        )
//    }
//
//    private fun decodeUrl(url: String): String {
//        return URLDecoder.decode(url, StandardCharsets.UTF_8.toString()).let { decodedUrl ->
//            // 무신사 URL만 허용하는 필터링 로직
//            if (decodedUrl.contains("musinsa")) {
//                URI.create(decodedUrl).toString()
//            } else {
//                throw ValidationException("지원하지 않는 URL 입니다.")
//            }
//        }
//    }
//
//    private fun fetchSoldoutCheckResponse(number: String): SoldoutCheckResponse {
//        val uri = buildSoldoutCheckUri(number)
//        log.info("Fetching soldout check response from URI: $uri")
//
//        return restClient.get().uri(uri).retrieve().body<SoldoutCheckResponse>() ?: throw MusinsaSoldoutCheckException()
//    }
//
//    private fun buildSoldoutCheckUri(number: String): String {
//        return StringConstant.MUSINSA_API_URL_TEMPLATE.get().format(number)
//    }
//
//    private fun extractOptions(response: SoldoutCheckResponse): Triple<List<String>, List<String>, List<String>> {
//        val firstOptions = response.data.basic[0].optionValues.map { it.name }
//        val secondOptions = response.data.basic.getOrNull(1)?.optionValues?.map { it.name } ?: emptyList()
//        val thirdOptions = response.data.basic.getOrNull(2)?.optionValues?.map { it.name } ?: emptyList()
//        return Triple(firstOptions, secondOptions, thirdOptions)
//    }
//
//    private fun extractJsonData(scriptContent: String, variableName: String): String? {
//        var jsonString: String? = null
//
//        // 자바스크립트 내 변수 선언 패턴
//        val pattern = "$variableName = "
//
//        // 패턴의 시작 위치 찾기
//        val startIndex = scriptContent.indexOf(pattern)
//
//        if (startIndex != -1) {
//            // 패턴 이후 부분 추출
//            val substring = scriptContent.substring(startIndex + pattern.length)
//
//            // JSON 데이터의 끝 위치 찾기
//            val endIndex = substring.indexOf("};") + 1
//
//            // JSON 문자열 추출
//            jsonString = substring.substring(0, endIndex)
//        }
//
//        return jsonString
//    }
//
//}
