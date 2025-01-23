package com.nbe2_3_3_team4.backend.security.service

import com.nbe2_3_3_team4.backend.domain.member.dto.MemberRequest
import com.nbe2_3_3_team4.backend.domain.member.entity.Member
import com.nbe2_3_3_team4.backend.domain.member.entity.enums.Role
import com.nbe2_3_3_team4.backend.domain.member.repository.MemberRepository
import com.nbe2_3_3_team4.backend.global.exception.ErrorCode
import com.nbe2_3_3_team4.backend.global.exception.GlobalExceptionHandler
import com.nbe2_3_3_team4.backend.global.exception.JWTCustomException
import com.nbe2_3_3_team4.backend.global.exception.NotFoundException
import com.nbe2_3_3_team4.backend.security.dto.TokenResponse
import com.nbe2_3_3_team4.backend.security.handler.JwtFilterExceptionHandler
import com.nbe2_3_3_team4.backend.security.util.TokenProvider
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.UnsupportedJwtException
import io.jsonwebtoken.security.SignatureException
import org.slf4j.LoggerFactory
import org.springframework.boot.context.event.ApplicationReadyEvent
import org.springframework.context.event.EventListener
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import java.util.function.Supplier

/**
 * member 관련 인증과 인가 담당하는 서비스 계층
 */
@Service
class CustomUserDetailService  (private val passwordEncoder: PasswordEncoder,
                                private val memberRepository: MemberRepository,
                                private val tokenProvider: TokenProvider,
                                private val authManagerBuilder: AuthenticationManagerBuilder) : UserDetailsService {


    private val log = LoggerFactory.getLogger(CustomUserDetailService::class.java)

    private inline fun <reified CustomUserDetailService> CustomUserDetailService.logger() = LoggerFactory.getLogger(CustomUserDetailService::class.java)!!


    @Throws(UsernameNotFoundException::class)
    override fun loadUserByUsername(email: String): UserDetails {
        return getUserDetails(findUserByEmail(email))
    }

    private fun getUserDetails(member: Member): UserDetails {
        return User(member.email, null, authorityMap[member.role])
    }

    fun findUserByEmail(userEmail: String?): Member {
        return memberRepository.findByEmail(userEmail)?.orElse(null) ?: throw  NotFoundException(ErrorCode.USER_NOT_FOUND)

    }

    //accessToken, refreshToken update
    fun updateToken(refreshToken: String?): TokenResponse.Create {
        try {
            val email: String = tokenProvider.findUser(refreshToken)
            val member = findUserByEmail(email)

            val authorities: List<GrantedAuthority> = authorityMap[member.role]!!

            val user = User(email, "password", authorities)
            // Authentication 객체 생성
            val authentication: Authentication = UsernamePasswordAuthenticationToken(user, null,
                    authorities)

            return issueAllTokens(authentication)
        } //refresh token parsing중 에러 발생(유효하지 않은 토큰인 경우) 처리
        catch (e: ExpiredJwtException) {
            log.info("refresh token 만료")
            throw JWTCustomException(ErrorCode.EXPIRED_REFRESH_TOKEN)
        } catch (e: UnsupportedJwtException) {
            log.info("유효하지 않은 refresh token")
            throw JWTCustomException(ErrorCode.INVALID_REFRESH_TOKEN)
        } catch (e: MalformedJwtException) {
            log.info("유효하지 않은 refresh token")
            throw JWTCustomException(ErrorCode.INVALID_REFRESH_TOKEN)
        } catch (e: SignatureException) {
            log.info("유효하지 않은 refresh token")
            throw JWTCustomException(ErrorCode.INVALID_REFRESH_TOKEN)
        } catch (e: IllegalArgumentException) {
            log.info("유효하지 않은 refresh token")
            throw JWTCustomException(ErrorCode.INVALID_REFRESH_TOKEN)
        }
    }


    fun issueAllTokens(authentication: Authentication): TokenResponse.Create {
        return tokenProvider.createAllToken(authentication)
    }


    fun login(dto: MemberRequest.Login): TokenResponse.Create {
        val email: String = dto.email

        val member: Member = memberRepository.findByEmail(email)?.orElse(null) ?: throw NotFoundException(ErrorCode.USER_NOT_FOUND)


        if (passwordEncoder.matches(dto.password, member.password)) {
            val authorities: List<GrantedAuthority> = authorityMap[member.role]!!

            val user = User(email, "password", authorities)

            // Authentication 객체 생성
            val authentication: Authentication = UsernamePasswordAuthenticationToken(user, null,
                    authorities)
            SecurityContextHolder.getContext().authentication = authentication

            return issueAllTokens(authentication)
        } else {
            throw NotFoundException(ErrorCode.INVALID_PASSWORD)
        }
    }

    @EventListener(ApplicationReadyEvent::class)
    fun setAuthoritiesMap() {
        val generalAuthorities: MutableList<GrantedAuthority> = ArrayList<GrantedAuthority>()
        generalAuthorities.add(SimpleGrantedAuthority("ROLE_USER"))

        val adminAuthorities: MutableList<GrantedAuthority> = ArrayList<GrantedAuthority>()
        adminAuthorities.add(SimpleGrantedAuthority("ROLE_USER"))
        adminAuthorities.add(SimpleGrantedAuthority("ROLE_ADMIN"))

        authorityMap[Role.ROLE_USER] = generalAuthorities
        authorityMap[Role.ROLE_ADMIN] = adminAuthorities
    }

    companion object {
        private val authorityMap: MutableMap<Role, List<GrantedAuthority>> = HashMap<Role, List<GrantedAuthority>>()
    }
}
