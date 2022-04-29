package com.github.toastshaman.springbootmasterclass.ping.api

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import javax.servlet.http.HttpServletRequest
import javax.validation.Valid

@RestController
class PingController {

    @GetMapping("/ping")
    fun ping() = ResponseEntity.ok(PingResponse("pong"))

    @PostMapping("/ping/{name}")
    fun pingWithName(
        @PathVariable("name") name: String,
        @Valid @RequestBody pingRequest: PingRequest,
        request: HttpServletRequest
    ) = ResponseEntity.ok(PingResponse("""${pingRequest.message} $name"""))
}