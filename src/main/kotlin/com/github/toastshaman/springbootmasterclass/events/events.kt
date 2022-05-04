package com.github.toastshaman.springbootmasterclass.events

import com.github.toastshaman.springbootmasterclass.events.EventCategory.INFO
import org.json.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.Clock
import java.time.Instant
import java.util.concurrent.LinkedBlockingDeque

enum class EventCategory {
    INFO, WARN, ERROR, METRICS
}

data class Event(
    val name: String,
    val category: EventCategory,
    val payload: JSONObject,
)

class EventBuilder(
    private val name: String,
    private val category: EventCategory = INFO,
    private val payload: JSONObject = JSONObject()
) {

    fun put(key: String, value: Any?) = apply { payload.putOnce(key, value) }

    fun build() = Event(
        name = name,
        category = category,
        payload = payload
    )
}

object EventEnvelope {
    operator fun invoke(clock: Clock): (Event) -> JSONObject = { event ->
        val metadata = JSONObject().apply {
            put("timestamp", Instant.now(clock).toString())
            put("name", event.name)
            put("category", event.category)
        }

        val envelope = JSONObject().apply {
            put("metadata", metadata)
            put("event", event.payload)
        }

        envelope
    }
}

fun interface Events {
    fun log(event: Event)

    companion object {
        fun builder(name: String) = EventBuilder(name)
    }
}

class LoggingEvents(private val clock: Clock) : Events {
    private val log = LoggerFactory.getLogger("Events")

    override fun log(event: Event) {
        log.info(EventEnvelope(clock)(event).toString())
    }
}

class PrintingEvents(private val clock: Clock = Clock.systemUTC()) : Events {
    override fun log(event: Event) {
        println(EventEnvelope(clock)(event).toString())
    }
}

class CompositeEvents(private val delegates: List<Events>) : Events {
    override fun log(event: Event) {
        delegates.forEach { it.log(event) }
    }

    companion object {
        fun of(vararg delegates: Events) = CompositeEvents(delegates.toList())
    }
}

class CapturingEvents(maxCapacity: Int = 500) : Events {
    val captured: LinkedBlockingDeque<Event> = LinkedBlockingDeque(maxCapacity)
    override fun log(event: Event) {
        captured.add(event)
    }
}

@Configuration
class EventsConfiguration {
    @Bean
    fun events() = LoggingEvents(Clock.systemUTC())
}
