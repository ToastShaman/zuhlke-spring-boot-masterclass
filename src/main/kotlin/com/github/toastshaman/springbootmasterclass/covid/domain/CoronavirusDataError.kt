package com.github.toastshaman.springbootmasterclass.covid.domain

import com.fasterxml.jackson.annotation.JsonProperty
import java.io.IOException

data class CoronavirusDataError(
    @JsonProperty("response") val response: String,
    @JsonProperty("status_code") val statusCode: Int,
    @JsonProperty("status") val status: String
)

class CoronavirusDataException(val error: CoronavirusDataError) : IOException(error.toString())
