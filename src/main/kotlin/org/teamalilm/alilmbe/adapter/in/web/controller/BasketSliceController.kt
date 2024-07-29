package org.teamalilm.alilmbe.adapter.`in`.web.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import org.springdoc.core.annotations.ParameterObject
import org.springframework.data.domain.Slice
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.teamalilm.alilmbe.application.port.`in`.use_case.BasketSliceUseCase
import org.teamalilm.alilmbe.web.adapter.error.RequestValidateException

@RestController
@RequestMapping("/api/v1/baskets")
@Tag(name = "products", description = "상품 전체 조회 api")
class BasketSliceController(
    private val basketSliceUseCase: BasketSliceUseCase
) {

    @Operation(
        summary = "상품 조회 API",
        description = """
            사용자들이 등록한 상품을 조회할 수 있는 기능을 제공해요.
            정렬 조건, 페이지, 사이즈를 입력받아요.
            
            기본은 기다리는 사람이 많은 순이며 같다면 상품명 순 입니다.
            
            기다리는 사람 순, 업데이트된 최신 순 으로 정렬 가능해요.
    """
    )
    @GetMapping
    fun productSlice(
        @ParameterObject
        @Valid
        productListParameter: ProductListParameter,

        bindingResult: BindingResult
    ): ResponseEntity<Slice<ProductListResponse>> {
        if (bindingResult.hasErrors()) {
            throw RequestValidateException(bindingResult)
        }
        val command = BasketSliceUseCase.BasketListCommand.from(productListParameter)

        val resultSlice = basketSliceUseCase.basketSlice(command)

        return ResponseEntity.ok(resultSlice.map { ProductListResponse.from(it) })
    }

    @Schema(description = "상품 조회 파라미터")
    data class ProductListParameter(
        @NotBlank(message = "사이즈는 필수에요.")
        @Min(value = 1, message = "사이즈는 1 이상이어야 합니다.")
        @Schema(description = "페이지 사이즈", defaultValue = "10")
        val size: Int,

        @NotBlank(message = "페이지 번호는 필수에요.")
        @Schema(description = "페이지 번호", defaultValue = "0")
        @Min(value = 0, message = "페이지 번호는 1 이상이어야 합니다.")
        val page: Int
    )

    data class ProductListResponse(
        val id: Long,
        val number: Long,
        val name: String,
        val brand: String,
        val imageUrl: String,
        val category: String,
        val price: Int,
        val option1: String,
        val option2: String?,
        val option3: String?,
        val waitingCount: Long,
    ) {

        companion object {
            fun from(result: BasketSliceUseCase.BasketListResult): ProductListResponse {
                return ProductListResponse(
                    id = result.id,
                    number = result.number,
                    name = result.name,
                    brand = result.brand,
                    imageUrl = result.imageUrl,
                    category = result.category,
                    price = result.price,
                    option1 = result.option1,
                    option2 = result.option2,
                    option3 = result.option3,
                    waitingCount = result.waitingCount,
                )
            }
        }
    }

    //        val pageRequest = PageRequest.of(
//            productListParameter.page,
//            productListParameter.size,
//            Sort.by(Sort.Direction.DESC, "id")
//        )
//
//        val command = BasketFindAllCommand(pageRequest)
//
//        val result = productListService.listProduct(command)
//
//        val response = result.map {
//            ProductListResponse(
//                id = it.id,
//                name = it.name,
//                brand = it.brand,
//                imageUrl = it.imageUrl,
//                price = it.price,
//                category = it.category,
//                productInfo = it.productInfo,
//                waitingCount = it.waitingCount,
//                oldestCreationTime = it.oldestCreationTime
//            )
//        }
}