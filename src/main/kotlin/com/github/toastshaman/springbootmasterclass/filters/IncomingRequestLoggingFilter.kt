package com.github.toastshaman.springbootmasterclass.filters

import org.jose4j.json.internal.json_simple.JSONObject
import org.slf4j.LoggerFactory
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Duration
import java.time.Instant
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class IncomingRequestLoggingFilter : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger("IncomingHttpRequest")

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val start = Instant.now()
        filterChain.doFilter(request, response)
        val duration = Duration.between(start, Instant.now())

        val headers = request
            .headerNames
            .asSequence()
            .map { it to request.getHeader(it) }
            .fold(JSONObject()) { obj, v -> obj.apply { put(v.first, v.second) } }

        val json = JSONObject().apply {
            put("status", response.status)
            put("method", request.method)
            put("path", request.requestURI)
            put("queryString", request.queryString)
            put("headers", headers)
            put("duration", duration.toString())
            put("duration_ms", duration.toMillis())
        }

        log.info(json.toJSONString())
    }
}

@Configuration
class RequestLoggingConfiguration {

    @Bean
    fun incomingRequestLoggingFilter() = FilterRegistrationBean<IncomingRequestLoggingFilter>()
        .apply {
            filter = IncomingRequestLoggingFilter()
            order = 1
        }
}