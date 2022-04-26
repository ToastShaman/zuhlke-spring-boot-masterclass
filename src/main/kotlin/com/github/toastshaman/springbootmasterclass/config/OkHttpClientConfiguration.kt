package com.github.toastshaman.springbootmasterclass.config

import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level.BODY
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.SECONDS

@Configuration
class OkHttpClientConfiguration {

    @Bean
    fun okHttpClient() = OkHttpClient.Builder()
        .addInterceptor(HttpLoggingInterceptor(::println).setLevel(BODY))
        .connectionPool(ConnectionPool(20,60, SECONDS))
        .build()
}