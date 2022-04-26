package com.github.toastshaman.springbootmasterclass.covid.client

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.toastshaman.springbootmasterclass.covid.domain.MetricEnvelope
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.valueOrNull
import java.time.Duration

class CachingCoronavirusData(private val delegate: CoronavirusData) : CoronavirusData {

    private val internalKey = "CACHE"

    private val cache = Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofHours(24))
        .build<String, MetricEnvelope> {
            delegate.newCasesByPublishDate().valueOrNull()
        }

    override fun newCasesByPublishDate() = when (val envelope = cache.get(internalKey)) {
        null -> Failure(IllegalStateException("could not retrieve metrics"))
        else -> Success(envelope)
    }
}