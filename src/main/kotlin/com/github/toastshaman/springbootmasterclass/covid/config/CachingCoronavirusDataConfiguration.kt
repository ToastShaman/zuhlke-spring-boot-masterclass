package com.github.toastshaman.springbootmasterclass.covid.config

import com.github.toastshaman.springbootmasterclass.covid.client.CachingCoronavirusData
import com.github.toastshaman.springbootmasterclass.covid.client.SyncCoronavirusData
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class CachingCoronavirusDataConfiguration(
    private val coronavirusData: SyncCoronavirusData
) {
    @Bean("caching-coronavirus-data")
    fun cachingCoronavirusData() = CachingCoronavirusData(coronavirusData)
}