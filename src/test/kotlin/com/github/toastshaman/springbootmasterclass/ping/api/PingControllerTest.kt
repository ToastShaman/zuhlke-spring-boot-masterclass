package com.github.toastshaman.springbootmasterclass.ping.api

import assertions.statusCodeIs
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isNotNull

class PingControllerTest {

    @Test
    fun `returns pong`() {
        val response = PingController().ping()

        expectThat(response)
            .statusCodeIs(200)
            .get { body }
            .isNotNull()
            .isEqualTo(PingResponse("pong"))
    }
}