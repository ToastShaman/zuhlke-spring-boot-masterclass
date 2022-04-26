package com.github.toastshaman.springbootmasterclass.covid.client

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.github.toastshaman.springbootmasterclass.covid.domain.CoronavirusDataConfiguration
import com.github.toastshaman.springbootmasterclass.covid.domain.CoronavirusDataException
import com.github.toastshaman.springbootmasterclass.covid.domain.MetricEnvelope
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.resultFrom
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.springframework.stereotype.Service
import java.net.URL

interface SyncCoronavirusData: CoronavirusData

@Service
class SyncHttpCoronavirusData(
    private val config: CoronavirusDataConfiguration,
    private val client: OkHttpClient,
    private val mapper: ObjectMapper
) : SyncCoronavirusData {

    override fun newCasesByPublishDate(): Result<MetricEnvelope, Exception> {
        val httpUrl = URL(config.baseUrl)
            .toHttpUrlOrNull()!!
            .newBuilder()
            .addQueryParameter("filters", "areaType=nation;areaName=england")
            .addQueryParameter("structure", """{"date":"date","value":"newCasesByPublishDate"}""")
            .addPathSegments("v1/data")
            .build()

        val request = Request.Builder().get()
            .url(httpUrl)
            .addHeader("Accept", "application/json")
            .build()

        return resultFrom {
            client.newCall(request).execute().use { resp ->
                when {
                    resp.isSuccessful -> resp.bodyOrThrow(mapper)
                    else -> throw CoronavirusDataException(resp.bodyOrThrow(mapper))
                }
            }
        }
    }
}

inline fun <reified T> Response.bodyOrThrow(mapper: ObjectMapper): T =
    body?.byteStream()?.use { inp -> mapper.readValue<T>(inp) } ?: error("received empty body")
