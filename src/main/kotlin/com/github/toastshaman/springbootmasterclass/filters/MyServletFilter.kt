package com.github.toastshaman.springbootmasterclass.filters

import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

class MyServletFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        response.addHeader("My-Custom-Header", "foobar")
        filterChain.doFilter(request, response)
    }
}

@Configuration
class MyServletFilterConfiguration {
    @Bean
    fun myServletFilter() = FilterRegistrationBean<MyServletFilter>()
        .apply {
            filter = MyServletFilter()
            order = Int.MAX_VALUE
        }
}
