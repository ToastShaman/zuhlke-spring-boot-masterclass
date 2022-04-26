package com.github.toastshaman.springbootmasterclass.covid.client

import com.github.toastshaman.springbootmasterclass.covid.domain.CoronavirusDataConfiguration
import com.github.toastshaman.springbootmasterclass.covid.domain.MetricEnvelope
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo
import com.github.tomakehurst.wiremock.junit5.WireMockTest
import dev.forkhandles.result4k.Success
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.junit.jupiter.api.Test
import setup.okJsonFromFile
import setup.testMapper
import strikt.api.expectThat
import strikt.assertions.isA

@WireMockTest
class SyncHttpCoronavirusDataTest {

    @Test
    fun `retrieves coronavirus data`(runtimeInfo: WireMockRuntimeInfo) {
        stubFor(get(urlPathEqualTo("/v1/data"))
            .withQueryParam("filters", equalTo("areaType=nation;areaName=england"))
            .withQueryParam("structure", equalTo( """{"date":"date","value":"newCasesByPublishDate"}"""))
            .willReturn(okJsonFromFile("api.coronavirus.data.200.json")))

        val configuration = CoronavirusDataConfiguration(baseUrl = "http://localhost:${runtimeInfo.httpPort}")

        val client = OkHttpClient.Builder().addInterceptor(
            HttpLoggingInterceptor(::println).setLevel(
                HttpLoggingInterceptor.Level.BODY
            )).build()

        val coronavirusData = SyncHttpCoronavirusData(
            config = configuration,
            client = client,
            mapper = testMapper
        )

        val response = coronavirusData.newCasesByPublishDate()

        expectThat(response)
            .isA<Success<MetricEnvelope>>()
    }
}