package org.team_alilm.adapter.out.persistence.mapper

import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.entity.AlilmJpaEntity
import domain.Alilm

@Component
class AlilmMapper {

    fun mapToJpaEntity(alilm: Alilm): AlilmJpaEntity {
        return AlilmJpaEntity(
            id = alilm.id.value,
            memberId = alilm.memberId,
            productId = alilm.productId,
        )
    }

    fun mapToDomainEntity(alilmJpaEntity: AlilmJpaEntity): Alilm {
        return Alilm(
            id = Alilm.AlilmId(alilmJpaEntity.id),
            memberId = alilmJpaEntity.memberId,
            productId = alilmJpaEntity.productId
        )
    }
}