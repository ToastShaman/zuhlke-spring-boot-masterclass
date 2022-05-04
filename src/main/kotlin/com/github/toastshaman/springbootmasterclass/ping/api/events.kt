package com.github.toastshaman.springbootmasterclass.ping.api

import com.github.toastshaman.springbootmasterclass.events.Events

object PingEvent {
    operator fun invoke(name: String) = Events
        .builder("PingEventReceived")
        .put("name", name)
        .put("message", "Received a ping")
        .build()
}