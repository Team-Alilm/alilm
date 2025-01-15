package org.team_alilm.gateway

import org.jsoup.nodes.Document

interface CrawlingGateway {

    fun htmlCrawling(request: CrawlingGatewayRequest) : CrawlingGatewayResponse

    data class CrawlingGatewayRequest(
        val url: String,
    )

    data class CrawlingGatewayResponse(
        val document: Document,
    )
}

