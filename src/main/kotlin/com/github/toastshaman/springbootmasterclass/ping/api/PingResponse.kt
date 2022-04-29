package com.github.toastshaman.springbootmasterclass.ping.api

import io.micrometer.core.lang.NonNull
import org.hibernate.validator.constraints.Length
import javax.validation.constraints.NotEmpty

data class PingResponse(val message: String)

data class PingRequest(
    @field:NonNull
    @field:NotEmpty
    @field:Length(max = 5)
    val message: String
)
