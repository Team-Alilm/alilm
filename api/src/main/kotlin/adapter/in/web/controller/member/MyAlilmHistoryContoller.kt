package org.team_alilm.adapter.`in`.web.controller.member

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.team_alilm.adapter.`in`.web.controller.product.ProductDetailsController.ProductDetailsResponse
import org.team_alilm.application.port.`in`.use_case.MyAlilmHistoryUseCase
import org.team_alilm.application.port.`in`.use_case.MyAlilmHistoryUseCase.MyAlilmHistoryResult
import org.team_alilm.application.port.`in`.use_case.ProductDetailsUseCase.ProductDetailsResult
import org.team_alilm.data.CustomMemberDetails
import org.team_alilm.domain.product.Product
import org.team_alilm.domain.product.Store

@RestController
@RequestMapping("/api/v1/member")
@Tag(name = "내 알림 히스토리 API", description = "내 알림 히스토리 관련 API")
class MyAlilmHistoryContoller(
    private val myAlilmHistoryUseCase: MyAlilmHistoryUseCase
) {
    
    @Operation(
        summary = "내 알림 히스토리 조회 API",
        description = """
            내 알림 히스토리를 조회합니다.
            (오늘 - 31일)까지 조회합니다.
            알림 발송된 시간 기준 내림차순으로 정렬합니다.
        """)
    @GetMapping("/my-alilm-history")
    fun myAlilmHistory(
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ) : ResponseEntity<MyAlilmHistoryResponse> {
        val command = MyAlilmHistoryUseCase.MyAlilmHistoryCommand(customMemberDetails.member)
        val result = myAlilmHistoryUseCase.myAlilmHistory(command)
        val response = MyAlilmHistoryResponse.from(result)

        return ResponseEntity.ok(response)
    }

    data class MyAlilmHistoryResponse(
        val alimHistoryList: List<AlilmHistory>
    ) {
        companion object {
            fun from (myAlilmHistoryResultList: List<MyAlilmHistoryResult>): MyAlilmHistoryResponse {
                return MyAlilmHistoryResponse(
                    alimHistoryList = myAlilmHistoryResultList.map { AlilmHistory.from(it) }
                )
            }
        }
    }

    data class AlilmHistory(
        val alilmId: Long,
        val productid: Long,
        val name: String,
        val imageUrl: String,
        val brand: String,
        val price: Int,
        val firstOption: String,
        val secondOption: String?,
        val thirdOption: String?
    ) {
        companion object {
            fun from(myAlilmHistoryResult: MyAlilmHistoryResult): AlilmHistory {
                return AlilmHistory(
                    alilmId = myAlilmHistoryResult.alilmId,
                    productid = myAlilmHistoryResult.productid,
                    name = myAlilmHistoryResult.name,
                    imageUrl = myAlilmHistoryResult.imageUrl,
                    brand = myAlilmHistoryResult.brand,
                    price = myAlilmHistoryResult.price,
                    firstOption = myAlilmHistoryResult.firstOption,
                    secondOption = myAlilmHistoryResult.secondOption,
                    thirdOption = myAlilmHistoryResult.thirdOption
                )
            }
        }
    }
}