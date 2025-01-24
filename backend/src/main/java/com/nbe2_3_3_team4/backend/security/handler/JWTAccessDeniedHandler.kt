package com.nbe2_3_3_team4.backend.security.handler

import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import java.io.IOException

//인증된 유저이나, 해당 자원에 접근할 수 없을 때 발생하는 예외를 처리
@Component
class JWTAccessDeniedHandler : AccessDeniedHandler {

    private val log = LoggerFactory.getLogger(JWTAccessDeniedHandler::class.java)

    private inline fun <reified JWTAccessDeniedHandler> JWTAccessDeniedHandler.logger() = LoggerFactory.getLogger(JWTAccessDeniedHandler::class.java)!!

    @Throws(IOException::class, ServletException::class)
    override fun handle(request: HttpServletRequest, response: HttpServletResponse, accessDeniedException: AccessDeniedException?) {
        log.error("Not Authenticated Request", accessDeniedException)
        log.error("Request Uri : {}", request.requestURI)
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.status = HttpStatus.FORBIDDEN.value()
        response.characterEncoding = "UTF-8"
    }
}