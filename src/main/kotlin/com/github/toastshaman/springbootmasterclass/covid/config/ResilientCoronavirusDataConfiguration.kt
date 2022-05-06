package com.github.toastshaman.springbootmasterclass.covid.config

import com.github.toastshaman.springbootmasterclass.covid.client.ResilientCoronavirusData
import com.github.toastshaman.springbootmasterclass.covid.client.SyncCoronavirusData
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ResilientCoronavirusDataConfiguration(
    private val coronavirusData: SyncCoronavirusData
) {
    @Bean("resilient-coronavirus-data")
    fun resilientCoronavirusData(factory: CircuitBreakerFactory<*, *>) =
        ResilientCoronavirusData(coronavirusData, factory)
}