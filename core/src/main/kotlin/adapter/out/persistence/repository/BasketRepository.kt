package org.team_alilm.adapter.out.persistence.repository

import jakarta.persistence.Tuple
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Slice
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.team_alilm.adapter.out.persistence.entity.BasketJpaEntity

interface BasketRepository : JpaRepository<BasketJpaEntity, Long> {

    @Query(
        """
            SELECT p as productJpaEntity, COUNT(b) as waitingCount
            FROM BasketJpaEntity b
            JOIN b.productJpaEntity p
            on b.productJpaEntity.id = p.id
            where b.isDelete = false
            and p.isDelete = false
            and b.isAlilm = false
            and b.isHidden = false
            GROUP BY p.id
            ORDER BY COUNT(b) DESC, b.id ASC
        """
    )
    fun loadBasketSlice(pageRequest: PageRequest): Slice<Tuple>

}

