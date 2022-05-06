package com.github.toastshaman.springbootmasterclass.covid.client

import dev.forkhandles.result4k.orThrow
import dev.forkhandles.result4k.resultFrom
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory

class ResilientCoronavirusData(
    private val delegate: CoronavirusData,
    private val factory: CircuitBreakerFactory<*, *>
) : CoronavirusData {

    override fun newCasesByPublishDate() = resultFrom {
        factory.create("covid").run { delegate.newCasesByPublishDate().orThrow() }
    }
}
