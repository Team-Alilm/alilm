package org.teamalilm.alilmbe.adapter.`in`.web.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.slf4j.LoggerFactory
import org.springdoc.core.annotations.ParameterObject
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.teamalilm.alilmbe.adapter.out.persistence.entity.product.Store
import org.teamalilm.alilmbe.application.port.`in`.use_case.ProductCrawlingCommand
import org.teamalilm.alilmbe.application.port.`in`.use_case.ProductCrawlingResult
import org.teamalilm.alilmbe.application.port.`in`.use_case.ProductCrawlingUseCase
import org.teamalilm.alilmbe.web.adapter.error.RequestValidateException

@RestController
@RequestMapping("/api/v1/products")
@Tag(name = "product-crawling", description = "상품 정보를 쇼핑몰에서 가져오는 API (현재 무신사만 지원)")
class ProductCrawlingController(
    private val useCase: ProductCrawlingUseCase
) {

    private val log = LoggerFactory.getLogger(ProductCrawlingController::class.java)

    @Operation(
        summary = "상품 크롤링을 실행하는 API",
        description = """
            사용자의 URL을 받아서 상품의 정보를 추출하는 API 입니다. 
            
            저희 서비스에 데이터를 저장하는 로직이 1도 없습니다.
            
            (현재 무신사만 지원해요.)
        """
    )
    @GetMapping("/crawling")
    fun scraping(
        @ParameterObject
        @Valid
        request: ProductScrapingRequest,

        bindingResult: BindingResult
    ) : ResponseEntity<ProductCrawlingResponse> {
        if (bindingResult.hasErrors()) {
            throw RequestValidateException(bindingResult)
        }

        return ResponseEntity.ok(
            ProductCrawlingResponse.from(
                useCase.invoke(
                    ProductCrawlingCommand.from(request)
                )
            )
        )
    }

    @Schema(description = "Scraping 요청")
    data class ProductScrapingRequest(
        @field:NotBlank(message = "URL은 필수입니다.")
        @field:Schema(description = "상품 URL (무신사 URL만 지원해요)", example = "https://www.musinsa.com/app/goods/3262292")
        val _url: String?
    ) {

        val url: String
            get() = _url!!
    }

    @Schema(description = "상품 데이터 응답")
    data class ProductCrawlingResponse(
        val number: Long,
        val name: String,
        val brand: String,
        val imageUrl: String,
        val category: String,
        val price: Int,
        val store: Store,
        val option1List: List<String>,
        val option2List: List<String>,
        val option3List: List<String>
    ) {

        companion object {
            fun from(result: ProductCrawlingResult): ProductCrawlingResponse {
                return ProductCrawlingResponse(
                    number = result.number,
                    name = result.name,
                    brand = result.brand,
                    imageUrl = result.imageUrl,
                    category = result.category,
                    price = result.price,
                    store = result.store,
                    option1List = result.option1List,
                    option2List = result.option2List,
                    option3List = result.option3List
                )
            }
        }
    }

}