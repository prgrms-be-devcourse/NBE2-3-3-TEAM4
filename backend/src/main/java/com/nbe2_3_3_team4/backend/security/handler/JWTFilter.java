package com.nbe2_3_3_team4.backend.security.handler;


import com.nbe2_3_3_team4.backend.global.exception.ErrorCode;
import com.nbe2_3_3_team4.backend.global.exception.JWTCustomException;
import com.nbe2_3_3_team4.backend.security.util.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UrlPathHelper;

import java.io.IOException;
import java.util.Arrays;

@Slf4j
@RequiredArgsConstructor
//@Component
public class JWTFilter extends OncePerRequestFilter {

	public static final String AUTHORIZATION_HEADER = "authorization";
	private final TokenProvider tokenProvider;


	// Request Header 에서 토큰 정보를 꺼내오기 위한 메소드
	private String resolveToken(String token) {
		String jwt = null;
		if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
			jwt = token.substring(7);
		}
		return jwt;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {

		HttpServletRequest httpServletRequest = request;
		String jwt = resolveToken(httpServletRequest.getHeader(AUTHORIZATION_HEADER));

		//토큰 정보 유효하면 securityContextHolder 에 사용자 인증 정보저장하고 다음 filter 진행
		//토큰 유효하지 않으면 JWTFilterExceptionHandler 에서 예외 처리
		if (StringUtils.hasText(jwt)) {
			if (tokenProvider.validateToken(jwt)) {
				Authentication authentication = tokenProvider.getAuthentication(jwt);
				SecurityContextHolder.getContext().setAuthentication(authentication);
				filterChain.doFilter(request, response);
			}
		} else {
			throw new JWTCustomException(ErrorCode.EMPTY_JWT_TOKEN);
		}
	}

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
		AntPathMatcher pathMatcher = new AntPathMatcher();

		String[] excludePath = {
			"/api/auth/signup",
			"/api/auth/login",
			"/api/auth/reissue",
			"/api/auth/signup/admin",
			"/ttukttak-parking/**",
			"/api/auth/kakao/search",
			"/api/parking/**",
		};

		String path = new UrlPathHelper().getPathWithinApplication(request);
		return Arrays.stream(excludePath)
			.anyMatch(pattern -> pathMatcher.match(pattern, path));
	}
}
