package com.github.toastshaman.springbootmasterclass.filters

import com.github.toastshaman.springbootmasterclass.events.Event
import com.github.toastshaman.springbootmasterclass.events.EventBuilder
import com.github.toastshaman.springbootmasterclass.events.Events
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Duration
import java.time.Instant
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class IncomingRequestLoggingFilter(private val events: Events) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val duration = measure { filterChain.doFilter(request, response) }
        events.log(IncomingHttpRequest(duration, request, response))
    }

    private inline fun measure(fn: () -> Unit): Duration {
        val start = Instant.now()
        fn()
        return Duration.between(start, Instant.now())
    }
}

object IncomingHttpRequest {
    operator fun invoke(
        duration: Duration,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Event {
        val headers = request
            .headerNames
            .asSequence()
            .associateBy({ it }, request::getHeader)

        return EventBuilder("IncomingHttpRequest")
            .put("status", response.status)
            .put("method", request.method)
            .put("path", request.requestURI)
            .put("queryString", request.queryString)
            .put("headers", headers)
            .put("duration", duration.toString())
            .put("duration_ms", duration.toMillis())
            .build()
    }
}

@Configuration
class RequestLoggingConfiguration(private val events: Events) {

    @Bean
    fun incomingRequestLoggingFilter() = FilterRegistrationBean<IncomingRequestLoggingFilter>()
        .apply {
            filter = IncomingRequestLoggingFilter(events)
            order = 1
        }
}