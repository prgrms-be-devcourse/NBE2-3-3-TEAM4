package com.nbe2_3_3_team4.backend.security.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.nbe2_3_3_team4.backend.global.exception.GlobalExceptionHandler
import com.nbe2_3_3_team4.backend.global.response.ApiResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import java.io.IOException
import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

//인증되지 않은 user가 특정 자원에 접근하고자할 때 발생하는 예외를 처리 + JWT토큰 예외 처리
@Component
class JWTAuthenticationEntryPoint @Autowired constructor(private val objectMapper: ObjectMapper) : AuthenticationEntryPoint {

    private val log = LoggerFactory.getLogger(JWTAuthenticationEntryPoint::class.java)

    private inline fun <reified JWTAuthenticationEntryPoint> JWTAuthenticationEntryPoint.logger() = LoggerFactory.getLogger(JWTAuthenticationEntryPoint::class.java)!!


    @Throws(IOException::class, ServletException::class)
    override fun commence(request: HttpServletRequest, response: HttpServletResponse, authException: AuthenticationException) {
        log.error("Not Authenticated Request", authException)
        log.error("Request Uri : {}", request.requestURI)

        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.characterEncoding = "UTF-8"

        val apiResponse = ApiResponse.createError(authException.message)
        val responseBody = objectMapper.writeValueAsString(apiResponse)
        response.writer.write(responseBody)
    }
}
