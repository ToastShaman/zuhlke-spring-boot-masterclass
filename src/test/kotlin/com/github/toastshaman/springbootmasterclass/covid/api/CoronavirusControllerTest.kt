package com.github.toastshaman.springbootmasterclass.covid.api

import assertions.statusCodeIs
import com.github.toastshaman.springbootmasterclass.covid.api.CoronavirusControllerTest.Mode.CHAOS
import com.github.toastshaman.springbootmasterclass.covid.api.CoronavirusControllerTest.Mode.FRIENDLY
import com.github.toastshaman.springbootmasterclass.covid.client.AsyncCoronavirusData
import com.github.toastshaman.springbootmasterclass.covid.client.CoronavirusData
import com.github.toastshaman.springbootmasterclass.covid.client.SyncCoronavirusData
import com.github.toastshaman.springbootmasterclass.covid.domain.MetricEnvelope
import com.github.toastshaman.springbootmasterclass.covid.domain.random
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isNotNull
import strikt.assertions.isNull

class CoronavirusControllerTest {

    @Test
    fun `returns 200 sync metrics`() {
        val response = CoronavirusController(
            async = NoopAsyncCoronavirusData(),
            sync = NoopSyncCoronavirusData()
        ).sync()

        expectThat(response)
            .statusCodeIs(200)
            .get { body }
            .isNotNull()
    }

    @Test
    fun `returns 500 sync metrics`() {
        val response = CoronavirusController(
            async = NoopAsyncCoronavirusData(CHAOS),
            sync = NoopSyncCoronavirusData(CHAOS)
        ).sync()

        expectThat(response)
            .statusCodeIs(500)
            .get { body }
            .isNull()
    }

    @Test
    fun `returns 200 async metrics`() {
        val response = CoronavirusController(
            async = NoopAsyncCoronavirusData(),
            sync = NoopSyncCoronavirusData()
        ).async()

        expectThat(response)
            .statusCodeIs(200)
            .get { body }
            .isNotNull()
    }

    @Test
    fun `returns 500 async metrics`() {
        val response = CoronavirusController(
            async = NoopAsyncCoronavirusData(CHAOS),
            sync = NoopSyncCoronavirusData(CHAOS)
        ).async()

        expectThat(response)
            .statusCodeIs(500)
            .get { body }
            .isNull()
    }

    enum class Mode {
        CHAOS, FRIENDLY
    }
    class NoopAsyncCoronavirusData(mode: Mode = FRIENDLY) : AsyncCoronavirusData, NoopCoronavirusData(mode)
    class NoopSyncCoronavirusData(mode: Mode = FRIENDLY) : SyncCoronavirusData, NoopCoronavirusData(mode)

    open class NoopCoronavirusData(private val chaos: Mode) : CoronavirusData {
        override fun newCasesByPublishDate() = when (chaos) {
            FRIENDLY -> Success(MetricEnvelope.random())
            CHAOS -> Failure(IllegalStateException("Something went wrong"))
        }
    }
}