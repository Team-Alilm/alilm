package org.team_alilm.adapter.out.persistence.repository.alilm

import org.team_alilm.adapter.out.persistence.entity.AlilmJpaEntity
import org.team_alilm.adapter.out.persistence.entity.ProductJpaEntity

data class AlilmAndProductProjection (
    val alilmJpaEntity: AlilmJpaEntity,
    val productJpaEntity: ProductJpaEntity
)