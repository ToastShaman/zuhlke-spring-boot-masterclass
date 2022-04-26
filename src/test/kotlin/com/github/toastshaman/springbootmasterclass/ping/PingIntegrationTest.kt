package com.github.toastshaman.springbootmasterclass.ping

import com.github.toastshaman.springbootmasterclass.ping.api.PingResponse
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody

@Tag("IntegrationTest")
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("integration")
class PingIntegrationTest {

    @Test
    fun `returns 200 OK`(@Autowired webClient: WebTestClient) {
        webClient
            .get()
            .uri("/ping")
            .accept(APPLICATION_JSON)
            .exchange()
            .expectStatus().is2xxSuccessful
            .expectBody<PingResponse>().isEqualTo(PingResponse("pong"))
    }
}