package com.nbe2_3_3_team4.backend.security.handler


import com.nbe2_3_3_team4.backend.global.exception.ErrorCode
import com.nbe2_3_3_team4.backend.global.exception.JWTCustomException
import com.nbe2_3_3_team4.backend.security.util.TokenProvider
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.util.AntPathMatcher
import org.springframework.util.StringUtils
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.UrlPathHelper
import java.io.IOException
import java.util.*
import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse

//@Component
class JWTFilter(private val tokenProvider: TokenProvider) : OncePerRequestFilter() {
    // Request Header 에서 토큰 정보를 꺼내오기 위한 메소드
    private fun resolveToken(token: String): String {

        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return  token.substring(7)
        }
        else{throw Exception() }
    }

    @Override
    @Throws(ServletException::class, IOException::class)
    override fun doFilterInternal(request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain) {
        val httpServletRequest: HttpServletRequest = request

        lateinit var jwt : String

        try{
            jwt = resolveToken(httpServletRequest.getHeader(AUTHORIZATION_HEADER)) }
        catch (e: Exception){
            throw JWTCustomException(ErrorCode.EMPTY_JWT_TOKEN)
        }

        //토큰 정보 유효하면 securityContextHolder 에 사용자 인증 정보저장하고 다음 filter 진행
        //토큰 유효하지 않으면 JWTFilterExceptionHandler 에서 예외 처리
        if (tokenProvider.validateToken(jwt)) {
            val authentication: Authentication = tokenProvider.getAuthentication(jwt)
            SecurityContextHolder.getContext().authentication = authentication
            filterChain.doFilter(request, response)
        }
    }
    @Throws(ServletException::class)
    override fun shouldNotFilter(request: HttpServletRequest): Boolean {
        val pathMatcher = AntPathMatcher()

        val excludePath = arrayOf(
                "/api/auth/signup",
                "/api/auth/login",
                "/api/auth/reissue",
                "/api/auth/signup/admin",
                "/ttukttak-parking/**",
                "/api/auth/kakao/search",
        )

        val path: String = UrlPathHelper().getPathWithinApplication(request)
        return Arrays.stream(excludePath)
                .anyMatch { pattern: String? -> pathMatcher.match(pattern!!, path) }
    }
    companion object {
        const val AUTHORIZATION_HEADER: String = "Authorization"
    }
}
