package com.github.toastshaman.springbootmasterclass.config

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.binder.okhttp3.OkHttpConnectionPoolMetrics
import io.micrometer.core.instrument.binder.okhttp3.OkHttpMetricsEventListener
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit.SECONDS

@Configuration
class OkHttpClientConfiguration(
    private val registry: MeterRegistry
) {

    @Bean
    fun okHttpClient(): OkHttpClient {
        val connectionPool = ConnectionPool(20, 60, SECONDS)
            .also { OkHttpConnectionPoolMetrics(it).bindTo(registry) }

        val eventListener = OkHttpMetricsEventListener
            .builder(registry, "coronavirus")
            .build()

        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor(::println).setLevel(BODY))
            .connectionPool(connectionPool)
            .eventListener(eventListener)
            .build()
    }
}