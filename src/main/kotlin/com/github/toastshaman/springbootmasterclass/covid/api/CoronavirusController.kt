package com.github.toastshaman.springbootmasterclass.covid.api

import com.github.toastshaman.springbootmasterclass.covid.client.AsyncCoronavirusData
import com.github.toastshaman.springbootmasterclass.covid.client.SyncCoronavirusData
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.recover
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/coronavirus")
class CoronavirusController(
    private val async: AsyncCoronavirusData,
    private val sync: SyncCoronavirusData,
) {
    @GetMapping("/sync/new-cases-by-publish-date")
    fun sync() = sync.newCasesByPublishDate()
        .map { ResponseEntity.ok(it.data) }
        .recover { ResponseEntity.internalServerError().build() }

    @GetMapping("/async/new-cases-by-publish-date")
    fun async() = async.newCasesByPublishDate()
        .map { ResponseEntity.ok(it.data) }
        .recover { ResponseEntity.internalServerError().build() }
}