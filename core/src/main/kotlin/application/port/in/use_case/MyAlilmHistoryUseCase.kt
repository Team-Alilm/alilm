package org.team_alilm.application.port.`in`.use_case

import org.team_alilm.domain.Member
import org.team_alilm.domain.product.Product

interface MyAlilmHistoryUseCase {

    fun myAlilmHistory(command: MyAlilmHistoryCommand): List<MyAlilmHistoryResult>

    data class MyAlilmHistoryCommand(
        val member: Member
    )

    data class MyAlilmHistoryResult(
        val alilmId: Long,
        val productid: Long,
        val name: String,
        val imageUrl: String,
        val brand: String,
        val price: Int,
        val firstOption: String,
        val secondOption: String?,
        val thirdOption: String?
    )
}