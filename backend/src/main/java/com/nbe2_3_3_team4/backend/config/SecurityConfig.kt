package com.nbe2_3_3_team4.backend.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.nbe2_3_3_team4.backend.security.handler.JWTAccessDeniedHandler
import com.nbe2_3_3_team4.backend.security.handler.JWTAuthenticationEntryPoint
import com.nbe2_3_3_team4.backend.security.handler.JWTFilter
import com.nbe2_3_3_team4.backend.security.handler.JwtFilterExceptionHandler
import com.nbe2_3_3_team4.backend.security.util.TokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.builders.WebSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer
import org.springframework.security.config.annotation.web.configurers.*
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig (private val jwtAuthenticationEntryPoint: JWTAuthenticationEntryPoint,
                      private val jwtAccessDeniedHandler: JWTAccessDeniedHandler,
                      private val tokenProvider: TokenProvider) {

    val frontUrl = arrayOf("/ttukttak_parking/**")
    val allowedUrl = arrayOf("/api/kakao/search", "/api/auth/**")

    @Bean
    @Throws(Exception::class)
    fun filterChain (http: HttpSecurity): SecurityFilterChain {

        return http
                .csrf { csrfConfig: CsrfConfigurer<HttpSecurity> -> csrfConfig.disable() }
                .headers { headerConfig: HeadersConfigurer<HttpSecurity?> ->
                    headerConfig.frameOptions { frameOptionsConfig -> frameOptionsConfig.disable() }
                }
                .cors { cors: CorsConfigurer<HttpSecurity?> -> cors.configurationSource(corsConfigurationSource()) } // CORS 관련
                .authorizeHttpRequests {
                    frontUrl.forEach { url ->
                        it.requestMatchers(AntPathRequestMatcher(url)).permitAll() }
                    allowedUrl.forEach { url->
                        it.requestMatchers(AntPathRequestMatcher(url)).permitAll() }
                        it.requestMatchers(AntPathRequestMatcher("/api/admin/**")).hasRole("ADMIN")
                                .anyRequest().authenticated()
                }
                .sessionManagement { sessionManagement: SessionManagementConfigurer<HttpSecurity?> -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
                .addFilterBefore(JWTFilter(tokenProvider),
                        UsernamePasswordAuthenticationFilter::class.java)
                .addFilterBefore(JwtFilterExceptionHandler(ObjectMapper()), JWTFilter::class.java)
                .exceptionHandling { exceptionHandling: ExceptionHandlingConfigurer<HttpSecurity?> ->
                    exceptionHandling
                            .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                            .accessDeniedHandler(jwtAccessDeniedHandler)
                }

                .build()
    }
    @Bean
    fun webSecurityCustomizer(): WebSecurityCustomizer {
        return WebSecurityCustomizer { web: WebSecurity ->
            web.ignoring()
                    .requestMatchers(AntPathRequestMatcher("/favicon.ico"))
                    .requestMatchers(AntPathRequestMatcher("/css/**"))
                    .requestMatchers(AntPathRequestMatcher("/js/**"))
                    .requestMatchers(AntPathRequestMatcher("/image/**"))
                    .requestMatchers(AntPathRequestMatcher("/swagger-ui/**"))
                    .requestMatchers(AntPathRequestMatcher("/test"))
                    .requestMatchers(AntPathRequestMatcher("/v3/api-docs/**"))
                    .requestMatchers(AntPathRequestMatcher("/swagger-ui.html"))
        }
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config: CorsConfiguration = CorsConfiguration()
        config.allowedOrigins = listOf("http://localhost:3000") // frontend url
        config.allowedMethods = mutableListOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
        config.allowedHeaders = listOf("*")
        config.allowCredentials = true
        config.maxAge = 3600L

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }
}
