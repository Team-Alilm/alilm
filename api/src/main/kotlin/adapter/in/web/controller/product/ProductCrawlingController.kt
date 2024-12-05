package org.team_alilm.adapter.`in`.web.controller.product

import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.adapter.`in`.web.controller.product.ProductSliceController.ProductListParameter
import org.team_alilm.application.port.`in`.use_case.ProductCrawlingUseCase
import org.team_alilm.domain.product.Store

@RestController
@Tag(name = "상품 크롤링 조회 API", description = "상품 크롤링 조회 API를 제공합니다.")
@RequestMapping("/api/v1/products")
class ProductCrawlingController(
    private val productCrawlingUseCase: ProductCrawlingUseCase
) {

    @GetMapping("/crawling")
    fun crawling(
        @ParameterObject
        @Valid
        productCrawlingParameter: ProductCrawlingParameter,
    ) : ResponseEntity<ProductCrawlingResponse> {
        val command = ProductCrawlingUseCase.ProductCrawlingCommand(
            url = productCrawlingParameter.url
        )

        val result = productCrawlingUseCase.crawling(command)
        val response = ProductCrawlingResponse.from(result)

        return ResponseEntity.ok(response)
    }

    data class ProductCrawlingParameter(
        val url: String
    )

    data class ProductCrawlingResponse(
        val productId: Long,
        val number: Long,
        val name: String,
        val brand: String,
        val thumbnailUrl: String,
        val store: Store,
        val price: Int,
        val category: String,
        val firstOption: String,
        val secondOption: String?,
        val thirdOption: String?,
    ) {

        companion object {
            fun from(productCrawlingResult: ProductCrawlingUseCase.CrawlingResult): ProductCrawlingResponse {
                return ProductCrawlingResponse(
                    productId = productCrawlingResult.id,
                    number = productCrawlingResult.number,
                    name = productCrawlingResult.name,
                    brand = productCrawlingResult.brand,
                    thumbnailUrl = productCrawlingResult.thumbnailUrl,
                    store = productCrawlingResult.store,
                    price = productCrawlingResult.price,
                    category = productCrawlingResult.category,
                    firstOption = productCrawlingResult.firstOption,
                    secondOption = productCrawlingResult.secondOption,
                    thirdOption = productCrawlingResult.thirdOption,
                )
            }
        }
    }
}