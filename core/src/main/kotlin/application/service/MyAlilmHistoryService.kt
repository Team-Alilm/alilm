package org.team_alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.team_alilm.application.port.`in`.use_case.MyAlilmHistoryUseCase
import org.team_alilm.application.port.out.LoadMyAlilmHistoryPort
import java.time.LocalDate
import java.time.ZoneOffset

@Service
@Transactional
class MyAlilmHistoryService(
    val loadMyAlilmHistoryPort: LoadMyAlilmHistoryPort
) : MyAlilmHistoryUseCase {

    override fun myAlilmHistory(command: MyAlilmHistoryUseCase.MyAlilmHistoryCommand): List<MyAlilmHistoryUseCase.MyAlilmHistoryResult> {
        val dayLimit: Long = LocalDate.now().plusDays(-31).atStartOfDay().toInstant(ZoneOffset.UTC).toEpochMilli()
        val myAlilmAndProductList = loadMyAlilmHistoryPort.loadMyAlilmHistory(command.member, dayLimit)

        return myAlilmAndProductList.map {
            MyAlilmHistoryUseCase.MyAlilmHistoryResult(
                alilmId = it.alilm.id.value!!,
                productid = it.product.id!!.value,
                name = it.product.name,
                imageUrl = it.product.thumbnailUrl,
                brand = it.product.brand,
                price = it.product.price,
                firstOption = it.product.firstOption,
                secondOption = it.product.secondOption,
                thirdOption = it.product.thirdOption
            )
        }
    }

}