package com.github.toastshaman.springbootmasterclass.covid

import com.github.tomakehurst.wiremock.client.WireMock.*
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import setup.jsonResponse
import setup.okJsonFromFile
import setup.verifyJsonStrictly

@Tag("IntegrationTest")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("integration")
class CoronavirusIntegrationTest {

    @Test
    fun `returns 200`(@Autowired webClient: WebTestClient) {
        stubFor(
            get(urlPathEqualTo("/v1/data"))
                .withQueryParam("filters", equalTo("areaType=nation;areaName=england"))
                .withQueryParam("structure", equalTo("""{"date":"date","value":"newCasesByPublishDate"}"""))
                .willReturn(okJsonFromFile("api.coronavirus.data.200.json"))
        )

        webClient
            .get()
            .uri("/v1/coronavirus/sync/new-cases-by-publish-date")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody()
            .verifyJsonStrictly()
    }

    @Test
    fun `returns 500`(@Autowired webClient: WebTestClient) {
        stubFor(
            get(urlPathEqualTo("/v1/data"))
                .withQueryParam("filters", equalTo("areaType=nation;areaName=england"))
                .withQueryParam("structure", equalTo("""{"date":"date","value":"newCasesByPublishDate"}"""))
                .willReturn(
                    jsonResponse(
                        500,
                        """{"response":"An internal error occurred whilst processing your request, please try again. If the problem persists, please report as an issue and include your request.","status_code":500,"status":"Internal Server Error"}"""
                    )
                )
        )

        webClient
            .get()
            .uri("/v1/coronavirus/sync/new-cases-by-publish-date")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().is5xxServerError
    }
}
