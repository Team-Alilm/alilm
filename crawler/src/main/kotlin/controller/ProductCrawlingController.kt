package org.team_alilm.controller

import domain.product.Store
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.use_case.ProductCrawlingUseCase
import org.team_alilm.application.port.use_case.ProductCrawlingUseCaseResolver
import org.team_alilm.error.NotFoundProductNumber

@RestController
@RequestMapping("/api/v1/products")
class ProductCrawlingController(
    private val productCrawlingUseCaseResolver: ProductCrawlingUseCaseResolver,
) {

    @GetMapping("/crawling")
    fun crawling(
        productCrawlingParameter: ProductCrawlingParameter,
    ) : ResponseEntity<ProductCrawlingResponse> {
        val store = productCrawlingParameter.getStore()
        val productNumber = productCrawlingParameter.getProductNumber()

        val command = ProductCrawlingUseCase.ProductCrawlingCommand(
            url = productCrawlingParameter.url,
            store = store,
            productNumber = productNumber
        )

        val productCrawlingUseCase = productCrawlingUseCaseResolver.resolve(store)
        val result = productCrawlingUseCase.crawling(command)
        val response = ProductCrawlingResponse.from(result)

        return ResponseEntity.ok(response)
    }

    data class ProductCrawlingParameter(
        val url: String
    ) {

        fun getStore(): Store {
            return when {
                url.contains("29cm") -> Store.CM29
                url.contains("musinsa") -> Store.MUSINSA
                url.contains("zigzag") -> Store.ZIGZAG
//                url.contains("a-bly") -> Store.A_BLY
                else -> throw IllegalArgumentException("지원하지 않는 URL입니다.")
            }
        }

        fun getProductNumber(): Long {
            // 정규식으로 6자리 이상의 숫자 추출
            val regex = "\\d{6,}".toRegex()
            return regex.find(url)?.value?.toLong() ?: throw NotFoundProductNumber()
        }
    }

    data class ProductCrawlingResponse(
        val number: Long,
        val name: String,
        val brand: String,
        val thumbnailUrl: String,
        val imageUrlList: List<String> = emptyList(),
        val store: Store,
        val price: Int,
        val firstCategory: String,
        val secondCategory: String?,
        val firstOptions: List<String>,
        val secondOptions: List<String> = emptyList(),
        val thirdOptions: List<String> = emptyList(),
    ) {

        companion object {
            fun from(productCrawlingResult: ProductCrawlingUseCase.CrawlingResult): ProductCrawlingResponse {
                return ProductCrawlingResponse(
                    number = productCrawlingResult.number,
                    name = productCrawlingResult.name,
                    brand = productCrawlingResult.brand,
                    thumbnailUrl = productCrawlingResult.thumbnailUrl,
                    imageUrlList = if (productCrawlingResult.imageUrlList.size > 3) {
                        productCrawlingResult.imageUrlList.shuffled().take(3)
                    } else {
                        productCrawlingResult.imageUrlList
                    },
                    store = productCrawlingResult.store,
                    price = productCrawlingResult.price,
                    firstCategory = productCrawlingResult.firstCategory,
                    secondCategory = productCrawlingResult.secondCategory,
                    firstOptions = productCrawlingResult.firstOptions,
                    secondOptions = productCrawlingResult.secondOptions,
                    thirdOptions = productCrawlingResult.thirdOptions
                )
            }
        }
    }
}