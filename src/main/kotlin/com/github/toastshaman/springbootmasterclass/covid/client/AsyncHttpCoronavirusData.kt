package com.github.toastshaman.springbootmasterclass.covid.client

import com.github.toastshaman.springbootmasterclass.covid.domain.CoronavirusDataConfiguration
import com.github.toastshaman.springbootmasterclass.covid.domain.CoronavirusDataError
import com.github.toastshaman.springbootmasterclass.covid.domain.CoronavirusDataException
import com.github.toastshaman.springbootmasterclass.covid.domain.MetricEnvelope
import dev.forkhandles.result4k.resultFrom
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.bodyToMono
import org.springframework.web.util.UriBuilder
import reactor.core.publisher.Mono
import java.net.URI

interface AsyncCoronavirusData: CoronavirusData

@Service
class AsyncHttpCoronavirusData(
    config: CoronavirusDataConfiguration,
    builder: WebClient.Builder,
) : AsyncCoronavirusData {

    private val client = builder
        .baseUrl(config.baseUrl)
        .build()

    override fun newCasesByPublishDate() = resultFrom {
        client.get()
            .uri(dataUri())
            .accept(APPLICATION_JSON)
            .retrieve()
            .onStatus(HttpStatus::isError) { resp ->
                resp.bodyToMono<CoronavirusDataError>()
                    .flatMap { Mono.error(CoronavirusDataException(it)) }
            }
            .bodyToMono<MetricEnvelope>()
            .block() ?: error("response is null")
    }

    private fun dataUri(): (UriBuilder) -> URI = { builder ->
        builder
            .pathSegment("v1", "data")
            .queryParam("filters", "areaType=nation;areaName=england")
            .queryParam("structure", "{structure}")
            .build("""{"date":"date","value":"newCasesByPublishDate"}""")
    }
}
