package org.teamalilm.alilm.adapter.`in`.web.controller

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.teamalilm.alilm.adapter.out.security.CustomMemberDetails
import org.teamalilm.alilm.application.port.`in`.use_case.AddFcmTokenUseCase

@RestController
@RequestMapping("/api/v1/fcm-tokens")
class AddFcmTokenController(
    private val addFcmTokenUseCase: AddFcmTokenUseCase
) {

    @PostMapping
    fun addFcmToken(
        @RequestBody @Valid fcmTokenRequest: FcmTokenRequest,
        @AuthenticationPrincipal customMemberDetails: CustomMemberDetails
    ) : ResponseEntity<Unit> {
        val command = AddFcmTokenUseCase.AddFcmTokenCommand(
            fcmToken = fcmTokenRequest.fcmToken,
            member = customMemberDetails.member
        )

        addFcmTokenUseCase.addFcmToken(command)

        return ResponseEntity.ok().build()
    }

    @Schema(description = "FCM 토큰 등록 요청")
    data class FcmTokenRequest(
        @field:NotBlank(message = "FCM 토큰은 필수입니다.")
        val fcmToken: String
    )
}