package org.team_alilm.adapter.out.persistence.repository.spring_data

import org.springframework.data.jpa.repository.JpaRepository
import org.team_alilm.adapter.out.persistence.entity.ProductImageJpaEntity
import org.team_alilm.domain.product.Store

interface SpringDataProductImageRepository : JpaRepository<ProductImageJpaEntity, Long> {

    fun existsByProductNumberAndProductStore(productNumber: Long, productStore: Store): Boolean
}