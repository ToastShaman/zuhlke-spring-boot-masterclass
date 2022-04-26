package setup

import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.*

fun okJsonFromFile(path: String): ResponseDefinitionBuilder = aResponse()
    .withStatus(200)
    .withHeader("Content-Type", "application/json")
    .withBodyFile(path)

fun jsonResponse(status: Int, body: String) = aResponse()
    .withStatus(status)
    .withHeader("Content-Type", "application/json")
    .withBody(body)