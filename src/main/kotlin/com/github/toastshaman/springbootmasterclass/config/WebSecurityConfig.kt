package com.github.toastshaman.springbootmasterclass.config

import org.jose4j.jws.AlgorithmIdentifiers.HMAC_SHA512
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.jose4j.keys.HmacKey
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.event.EventListener
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.http.SessionCreationPolicy.STATELESS
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse
import kotlin.random.Random


@Configuration
@EnableWebSecurity
class WebSecurityConfig(private val hmacKey: HmacKey) : WebSecurityConfigurerAdapter() {

    override fun configure(http: HttpSecurity) {
        http
            .csrf()
            .disable()
            .sessionManagement()
            .sessionCreationPolicy(STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/v1/**").permitAll()
            .antMatchers("/actuator/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(JwtTokenFilter(hmacKey), UsernamePasswordAuthenticationFilter::class.java)
    }
}

@Configuration
class JwtKeyConfiguration {
    @Bean
    fun hmacKey() = HmacKey(Random.nextBytes(128))
}


@Component
class JwtKeyPrinter(private val hmacKey: HmacKey) {
    @EventListener(ApplicationReadyEvent::class)
    fun printKey() {

        val claims = JwtClaims().apply {
            setExpirationTimeMinutesInTheFuture(10f)
            setGeneratedJwtId()
            setIssuedAtToNow()
            setNotBeforeMinutesInThePast(2f)
            subject = "subject"
        }

        val jws = JsonWebSignature().apply {
            algorithmHeaderValue = HMAC_SHA512
            payload = claims.toJson()
            key = hmacKey
        }

        println()
        println("""Valid Token: ${jws.compactSerialization}""")
    }
}

class JwtTokenFilter(hmacKey: HmacKey) : OncePerRequestFilter() {

    private val consumer = JwtConsumerBuilder()
        .setRequireExpirationTime()
        .setAllowedClockSkewInSeconds(30)
        .setVerificationKey(hmacKey)
        .build()

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val authorization = request.getHeader(AUTHORIZATION).orEmpty()
        if (!authorization.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authorization.removePrefix("Bearer ")
        val context = runCatching { consumer.process(token) }.getOrNull()
        if (context == null) {
            filterChain.doFilter(request, response)
            return
        }

        val authentication = UsernamePasswordAuthenticationToken(context, null, emptyList())
        SecurityContextHolder.getContext().authentication = authentication

        filterChain.doFilter(request, response)
    }
}