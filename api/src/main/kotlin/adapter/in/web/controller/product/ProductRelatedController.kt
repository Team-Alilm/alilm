package org.team_alilm.adapter.`in`.web.controller.product

import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.application.port.`in`.use_case.ProductRelatedUseCase
import org.team_alilm.application.port.`in`.use_case.ProductRelatedUseCase.*
import org.team_alilm.domain.product.Product

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "관련 상품 API", description = """
    관련 상품을 모아둔 컨트롤러 입니다.
    해당 상품 id 값을 보내주면 알려드릴게요.
    최대 10개를 반환 합니다.
""")
class ProductRelatedController(
    private val productRelatedUseCase: ProductRelatedUseCase
) {

    @GetMapping("/related/{productId}")
    fun productRecent(@PathVariable productId: Long) : ResponseEntity<ProductRelatedResponse> {
        val command = ProductRelatedCommand(productId = productId)
        val result = productRelatedUseCase.productRelated(command)
        val response = ProductRelatedResponse.from(result)

        return ResponseEntity.ok(response)
    }

    data class ProductRelatedResponse(
        val relatedProductList: List<RelatedProduct>
    ) {
        companion object {
            fun from(result: ProductRelatedResult): ProductRelatedResponse {
                return ProductRelatedResponse(
                    relatedProductList = result.productList.map { RelatedProduct.from(it) }
                )
            }
        }
    }

    data class RelatedProduct(
        val id: Long,
        val name: String,
        val imageUrl: String,
        val brand: String,
        val price: Int,
        val firstOption: String,
        val secondOption: String?,
        val thirdOption: String?
    ) {
        companion object {
            fun from(product: Product): RelatedProduct {
                return RelatedProduct(
                    id = product.id?.value ?: 0,
                    name = product.name,
                    imageUrl = product.thumbnailUrl,
                    brand = product.brand,
                    price = product.price,
                    firstOption = product.firstOption,
                    secondOption = product.secondOption,
                    thirdOption = product.thirdOption
                )
            }
        }
    }
}