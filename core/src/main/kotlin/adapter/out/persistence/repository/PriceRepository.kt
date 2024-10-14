package org.team_alilm.adapter.out.persistence.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.team_alilm.adapter.out.persistence.entity.PriceJpaEntity

interface PriceRepository : JpaRepository<PriceJpaEntity, Long> {
}
