package org.teamalilm.alilm.adapter.`in`.web.controller.baskets

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.teamalilm.alilm.adapter.out.security.CustomMemberDetails
import org.teamalilm.alilm.application.port.`in`.use_case.MyBasketsUseCase

@RestController
@RequestMapping("/api/v1/baskets")
@Tag(name = "나의 장바구니 조회 API", description = "나의 알림 page에서 사용하는 API를 제공합니다.")
class MyBasketsController(
    private val myBasketsUseCase: MyBasketsUseCase
) {

    @GetMapping("/my")
    @Operation(
        summary = "나의 장바구니 조회 API",
        description = """
            사용자가 등록한 상품을 조회할 수 있는 기능을 제공해요.
            등록한 상품을 조회할 수 있어요.
            
            나의 상품은 페이지 네이션을 넣을 만큼 절대적인 양이 많지 않다고 생각해요.
            때문에 한번에 모든 상품을 조회할 수 있어요.
        """
    )
    fun myBasket(
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ) : ResponseEntity<List<MyBasketsResponse>> {
        return ResponseEntity.ok(
            myBasketsUseCase.myBasket(
                MyBasketsUseCase.MyBasketCommand(customMemberDetails.member)
            ).map { MyBasketsResponse.from(it) }
        )
    }

    data class MyBasketsResponse(
        val id: Long,
        val number: Long,
        val name: String,
        val brand: String,
        val imageUrl: String,
        val store: String,
        val price: Int,
        val category: String,
        val firstOption: String,
        val secondOption: String?,
        val thirdOption: String?,
        val isHidden: Boolean
    ) {

        companion object {
            fun from(myBasketsResult: MyBasketsUseCase.MyBasketsResult) : MyBasketsResponse {
                return MyBasketsResponse(
                    id = myBasketsResult.id,
                    number = myBasketsResult.number,
                    name = myBasketsResult.name,
                    brand = myBasketsResult.brand,
                    imageUrl = myBasketsResult.imageUrl,
                    store = myBasketsResult.store,
                    price = myBasketsResult.price,
                    category = myBasketsResult.category,
                    firstOption = myBasketsResult.firstOption,
                    secondOption = myBasketsResult.secondOption,
                    thirdOption = myBasketsResult.thirdOption,
                    isHidden = myBasketsResult.isHidden
                )
            }
        }

    }

}