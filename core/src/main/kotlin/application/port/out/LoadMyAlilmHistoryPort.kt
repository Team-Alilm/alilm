package org.team_alilm.application.port.out

import org.team_alilm.domain.Alilm
import org.team_alilm.domain.Member
import org.team_alilm.domain.product.Product

interface LoadMyAlilmHistoryPort {

    fun loadMyAlilmHistory(member: Member, dayLimit: Long): List<MyAlilmHistory>

    data class MyAlilmHistory(
        val alilm: Alilm,
        val product: Product
    ) {
        companion object {
            fun of (alilm: Alilm, product: Product): MyAlilmHistory {
                return MyAlilmHistory(
                    alilm = alilm,
                    product = product
                )
            }
        }
    }
}