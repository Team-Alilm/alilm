package org.team_alilm.adapter.out.persistence.adapter

import org.springframework.stereotype.Component
import org.team_alilm.adapter.out.persistence.mapper.AlilmMapper
import org.team_alilm.adapter.out.persistence.mapper.ProductMapper
import org.team_alilm.adapter.out.persistence.repository.AlilmRepository
import org.team_alilm.application.port.out.LoadMyAlilmHistoryPort
import org.team_alilm.domain.Member
import org.team_alilm.global.error.NotFoundMemberException

@Component
class AlilmAndProductAdapter(
    private val alilmRepository: AlilmRepository,
    private val alilmMapper: AlilmMapper,
    private val productMapper: ProductMapper
) : LoadMyAlilmHistoryPort {

    override fun loadMyAlilmHistory(member: Member, dayLimit: Long): List<LoadMyAlilmHistoryPort.MyAlilmHistory> {
        return alilmRepository
               .findAlilmAndProductByMemberId(member.id?.value?: throw NotFoundMemberException(), dayLimit)
               .map {
                   LoadMyAlilmHistoryPort.MyAlilmHistory(
                       alilm = alilmMapper.mapToDomainEntity(it.alilmJpaEntity),
                       product = productMapper.mapToDomainEntity(it.productJpaEntity)
                   )
               }
    }
}