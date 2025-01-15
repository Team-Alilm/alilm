package org.team_alilm.application.port.use_case

import domain.product.Store
import org.springframework.stereotype.Component
//import org.team_alilm.application.service.AblyProductCrawlingService
import org.team_alilm.application.service.CM29ProductCrawlingService
import org.team_alilm.application.service.MusinsaProductCrawlingService
import org.team_alilm.application.service.ZigzagProductCrawlingService

@Component
class ProductCrawlingUseCaseResolver(
    private val muSinSaProductCrawlingUseCase: MusinsaProductCrawlingService,
//    private val aBlyProductCrawlingUseCase: AblyProductCrawlingService,
    private val cm29ProductCrawlingUseCase: CM29ProductCrawlingService,
    private val zigzagProductCrawlingUseCase: ZigzagProductCrawlingService
) {

    fun resolve(store: Store): ProductCrawlingUseCase {
        return when (store) {
            Store.CM29 -> cm29ProductCrawlingUseCase
            Store.MUSINSA -> muSinSaProductCrawlingUseCase
//            Store.A_BLY -> aBlyProductCrawlingUseCase
            Store.ZIGZAG -> zigzagProductCrawlingUseCase
            Store.NONE -> throw IllegalArgumentException("Unknown store")
        }
    }
}