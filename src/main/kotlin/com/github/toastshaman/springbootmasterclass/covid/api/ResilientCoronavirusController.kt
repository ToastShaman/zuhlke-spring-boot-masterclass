package com.github.toastshaman.springbootmasterclass.covid.api

import com.github.toastshaman.springbootmasterclass.covid.client.CoronavirusData
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.recover
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/coronavirus")
class ResilientCoronavirusController(
    @Qualifier("resilient-coronavirus-data") private val client: CoronavirusData,
) {
    @GetMapping("/resilient/new-cases-by-publish-date")
    fun cached() = client.newCasesByPublishDate()
        .map { ResponseEntity.ok(it.data) }
        .recover { ResponseEntity.internalServerError().build() }
}