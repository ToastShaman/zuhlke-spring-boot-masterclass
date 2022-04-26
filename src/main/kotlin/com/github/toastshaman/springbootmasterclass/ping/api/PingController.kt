package com.github.toastshaman.springbootmasterclass.ping.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class PingController {

    @GetMapping("/ping")
    fun ping() = ResponseEntity.ok(PingResponse("pong"))
}