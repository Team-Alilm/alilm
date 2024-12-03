package org.team_alilm.application.port.out.gateway

interface SendMailGateway {

    fun sendMail (
        to: String, nickname: String, productName: String, productNumber: Long, imageUrl: String, options: String
    )

}