package com.nbe2_3_3_team4.backend.security.handler


import com.fasterxml.jackson.databind.ObjectMapper
import com.nbe2_3_3_team4.backend.global.exception.ErrorCode
import com.nbe2_3_3_team4.backend.global.exception.GlobalExceptionHandler
import com.nbe2_3_3_team4.backend.global.exception.JWTCustomException
import com.nbe2_3_3_team4.backend.global.response.ApiResponse
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.SecurityException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

@Component
class JwtFilterExceptionHandler(val objectMapper: ObjectMapper) : OncePerRequestFilter() {

    private val log = LoggerFactory.getLogger(JwtFilterExceptionHandler::class.java)

    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        try {
            filterChain.doFilter(request, response)
        } catch (e: SecurityException) {
            log.info("invalidate jwt signature")
            throwException(ErrorCode.INVALID_ACCESS_TOKEN, response)
        } catch (e: MalformedJwtException) {
            log.info("invalidate jwt signature")
            throwException(ErrorCode.INVALID_ACCESS_TOKEN, response)
        } catch (e: ExpiredJwtException) {
            log.info("token이 만료되었습니다")
            throwException(ErrorCode.EXPIRED_ACCESS_TOKEN, response)
        } catch (e: UnsupportedJwtException) {
            log.info("unsupported jwt token")
            throwException(ErrorCode.INVALID_ACCESS_TOKEN, response)
        } catch (e: IllegalArgumentException) {
            log.info("invalid jwt token : " + e.message + e.cause)
            throwException(ErrorCode.INVALID_ACCESS_TOKEN, response)
        } catch (e: JWTCustomException) {
            log.info("jwt 토큰을 찾을 수 없습니다.")
            throwException(e.errorCode, response)
        }
    }

    @Throws(IOException::class)
    fun throwException(e: ErrorCode, response: HttpServletResponse) {
        val apiResponse = ApiResponse.createErrorWithMsg(e.message)
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"
        response.writer.write(objectMapper.writeValueAsString(apiResponse))
    }
}





