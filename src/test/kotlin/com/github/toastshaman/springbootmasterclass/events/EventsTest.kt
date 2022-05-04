package com.github.toastshaman.springbootmasterclass.events

import assertions.containsEventWithName
import assertions.isEqualToJson
import assertions.payload
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset.UTC

class EventsTest {

    private val capturingEvents = CapturingEvents()
    private val printingEvents = PrintingEvents(Clock.fixed(Instant.EPOCH, UTC))
    private val events = CompositeEvents.of(printingEvents, capturingEvents)

    @Test
    fun `logs event`() {
        val event = Events.builder("MyEvent").put("message", "hello").build()

        events.log(event)

        expectThat(capturingEvents)
            .containsEventWithName("MyEvent")
            .payload
            .isEqualToJson("""{"message":"hello"}""")
    }
}
