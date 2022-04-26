package com.github.toastshaman.springbootmasterclass.covid.client

import com.github.toastshaman.springbootmasterclass.covid.domain.CoronavirusDataConfiguration
import com.github.toastshaman.springbootmasterclass.covid.domain.MetricEnvelope
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import dev.forkhandles.result4k.Success
import org.junit.jupiter.api.Test
import org.springframework.web.reactive.function.client.WebClient
import setup.okJsonFromFile
import strikt.api.expectThat
import strikt.assertions.isA

@WireMockTest
class AsyncHttpCoronavirusDataTest {

    @Test
    fun `retrieves coronavirus data`(runtimeInfo: WireMockRuntimeInfo) {
        stubFor(
            get(urlPathEqualTo("/v1/data"))
                .withQueryParam("filters", equalTo("areaType=nation;areaName=england"))
                .withQueryParam("structure", equalTo("""{"date":"date","value":"newCasesByPublishDate"}"""))
                .willReturn(okJsonFromFile("api.coronavirus.data.200.json"))
        )

        val configuration = CoronavirusDataConfiguration(baseUrl = "http://localhost:${runtimeInfo.httpPort}")

        val client = WebClient.builder()

        val coronavirusData = AsyncHttpCoronavirusData(
            config = configuration,
            builder = client,
        )

        val response = coronavirusData.newCasesByPublishDate()

        expectThat(response)
            .isA<Success<MetricEnvelope>>()
    }
}