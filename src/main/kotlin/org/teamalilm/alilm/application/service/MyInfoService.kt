package org.teamalilm.alilm.application.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.teamalilm.alilm.application.port.`in`.use_case.MyInfoUseCase
import org.teamalilm.alilm.application.port.out.LoadMemberPort
import org.teamalilm.alilm.common.error.NotFoundMemberException

@Service
@Transactional
class MyInfoService(
    val loadMemberPort: LoadMemberPort
) : MyInfoUseCase {

    override fun myInfo(command: MyInfoUseCase.MyInfoCommand): MyInfoUseCase.MyInfoResult {
        val member = loadMemberPort.loadMember(command.member.id!!.value)
            ?: throw NotFoundMemberException()

        return MyInfoUseCase.MyInfoResult(
            nickname = member.nickname,
            email = member.email
        )
    }

}