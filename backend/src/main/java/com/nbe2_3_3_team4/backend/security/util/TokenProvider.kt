package com.nbe2_3_3_team4.backend.security.util

import com.nbe2_3_3_team4.backend.security.dto.TokenResponse
import io.jsonwebtoken.*
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import io.jsonwebtoken.security.SecurityException
import org.springframework.beans.factory.InitializingBean
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import java.security.Key
import java.util.*
import java.util.stream.Collectors

@Component
class TokenProvider(@param:Value("\${jwt.secret-key}") private val SECRET_KEY: String,
                    @Value("\${jwt.token-validity-in-seconds}") tokenValidityInSeconds: Long
) : InitializingBean {
    private val accessTokenValidityInMilliSeconds = tokenValidityInSeconds * 1000 //60분
    private val refreshTokenValidityInMilliSeconds = accessTokenValidityInMilliSeconds * 336 //14일
    private var key: Key? = null

    // BeanFactory 에 의해 모든 property 가 설정되고 난 뒤 실행되는 메소드
    // secret key 주입
    override fun afterPropertiesSet() {
        val keyBytes: ByteArray = Decoders.BASE64.decode(this.SECRET_KEY)
        key = Keys.hmacShaKeyFor(keyBytes)
    }

    // 토큰 생성
    fun createAllToken(authentication: Authentication): TokenResponse.Create {
        //비공개키 암호화 방식 사용

        val authorities = authentication.authorities.stream()
                .map { obj: GrantedAuthority -> obj.authority }
                .collect(Collectors.joining(","))

        return TokenResponse.Create.from(
                Jwts.builder()
                        .setSubject(authentication.name)
                        .claim(AUTHORITIES_KEY, authorities)
                        .signWith(key, SignatureAlgorithm.HS512)
                        .setExpiration(createTokenValidity(this.accessTokenValidityInMilliSeconds))
                        .compact(),
                Jwts.builder()
                        .setSubject(authentication.name)
                        .signWith(key, SignatureAlgorithm.HS512)
                        .setExpiration(createTokenValidity(this.refreshTokenValidityInMilliSeconds))
                        .compact(),
                authentication.name)
    }

    private fun createTokenValidity(milliseconds: Long): Date {
        return Date(Date().time + milliseconds)
    }

    fun getAuthentication(token: String?): Authentication {
        val claims: Claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .body

        val authorities: Collection<GrantedAuthority> =
                Arrays.stream(claims[AUTHORITIES_KEY].toString().split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray())
                        .map { role: String? -> SimpleGrantedAuthority(role) }
                        .collect(Collectors.toList())

        val user = User(claims.subject, "password", authorities)

        return UsernamePasswordAuthenticationToken(user, token, authorities)
    }

    fun findUser(refreshToken: String?): String {
        val claims: Claims = Jwts
                .parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(refreshToken)
                .body
        return claims.subject
    }

    // exception 발생 안하면 true 반환
    // exception 반환시 JwtFilterExceptionHandler 에서 처리
    @Throws(ExpiredJwtException::class, SecurityException::class, MalformedJwtException::class, UnsupportedJwtException::class, IllegalArgumentException::class)
    fun validateToken(token: String?): Boolean {
        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token)
        return true
    }


    companion object {
        private const val AUTHORITIES_KEY = "auth"
    }
}
