package com.github.toastshaman.springbootmasterclass.covid.domain

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "coronavirus")
data class CoronavirusDataConfiguration(var baseUrl: String = "")
