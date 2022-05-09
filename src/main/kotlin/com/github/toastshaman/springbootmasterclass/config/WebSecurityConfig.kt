package com.github.toastshaman.springbootmasterclass.config

import org.jose4j.jws.AlgorithmIdentifiers.HMAC_SHA512
import org.jose4j.jws.JsonWebSignature
import org.jose4j.jwt.JwtClaims
import org.jose4j.jwt.consumer.JwtConsumerBuilder
import org.jose4j.jwt.consumer.Validator
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
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Duration
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
            .antMatchers("/ping/**").permitAll()
            .antMatchers("/actuator/**").permitAll()
            .antMatchers("/admin/**").hasAnyAuthority(Roles.ADMIN)
            .anyRequest().authenticated()
            .and()
            .addFilterBefore(JwtTokenFilter(hmacKey), UsernamePasswordAuthenticationFilter::class.java)
    }
}

object Roles {
    const val ADMIN = "ADMIN"
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
            setExpirationTimeMinutesInTheFuture(Duration.ofMinutes(60))
            setNotBeforeMinutesInThePast(Duration.ofMinutes(2))
            setIssuedAtToNow()
            setStringListClaim("roles", listOf(Roles.ADMIN))
            issuer = "spring-boot-masterclass"
            subject = "alice@foobar.com"
        }

        val jws = JsonWebSignature().apply {
            algorithmHeaderValue = HMAC_SHA512
            payload = claims.toJson()
            key = hmacKey
        }

        println()
        println("""Valid Token: ${jws.compactSerialization}""")
    }

    private fun JwtClaims.setExpirationTimeMinutesInTheFuture(duration: Duration) {
        setExpirationTimeMinutesInTheFuture(duration.toMinutes().toFloat())
    }

    private fun JwtClaims.setNotBeforeMinutesInThePast(duration: Duration) {
        setNotBeforeMinutesInThePast(duration.toMinutes().toFloat())
    }
}

class JwtTokenFilter(hmacKey: HmacKey) : OncePerRequestFilter() {

    private val consumer = JwtConsumerBuilder()
        .setAllowedClockSkewInSeconds(30)
        .setRequireExpirationTime()
        .setRequireSubject()
        .setRequireNotBefore()
        .setExpectedIssuer("spring-boot-masterclass")
        .registerValidator(Validator { ctx -> if (ctx.jwtClaims.hasClaim("roles")) null else "missing claim [roles]" })
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

        SecurityContextHolder.getContext().authentication = UsernamePasswordAuthenticationToken(
            context,
            null,
            context.jwtClaims.getGrantedAuthorities()
        )

        filterChain.doFilter(request, response)
    }

    private fun JwtClaims.getGrantedAuthorities() = getStringListClaimValue("roles").map(::SimpleGrantedAuthority)
}