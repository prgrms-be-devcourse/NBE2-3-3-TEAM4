package com.nbe2_3_3_team4.backend.security.handler;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbe2_3_3_team4.backend.global.exception.ErrorCode;
import com.nbe2_3_3_team4.backend.global.exception.JWTCustomException;
import com.nbe2_3_3_team4.backend.global.response.ApiResponse;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SecurityException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilterExceptionHandler extends OncePerRequestFilter {

	private final ObjectMapper objectMapper;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {
		try {
			filterChain.doFilter(request, response);
		} catch (SecurityException | MalformedJwtException e) {
			log.info("invalidate jwt signature");
			throwException(ErrorCode.INVALID_ACCESS_TOKEN, response);
		} catch (ExpiredJwtException e) {
			log.info("token이 만료되었습니다");
			throwException(ErrorCode.EXPIRED_ACCESS_TOKEN, response);
		} catch (UnsupportedJwtException e) {
			log.info("unsupported jwt token");
			throwException(ErrorCode.INVALID_ACCESS_TOKEN, response);
		} catch (IllegalArgumentException e) {
			log.info("invalid jwt token : " + e.getMessage() + e.getCause());
			throwException(ErrorCode.INVALID_ACCESS_TOKEN, response);
		} catch (JWTCustomException e) {
			log.info("jwt 토큰을 찾을 수 없습니다.");
			throwException(e.getErrorCode(), response);
		}
	}

	public void throwException(ErrorCode e, HttpServletResponse response) throws IOException {

		ApiResponse<Object> apiResponse = ApiResponse.createErrorWithMsg(e.getMessage());
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setContentType(APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
	}

}





