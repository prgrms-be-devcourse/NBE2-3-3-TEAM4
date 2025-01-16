package com.nbe2_3_3_team4.backend.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbe2_3_3_team4.backend.global.response.ApiResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

//인증되지 않은 user가 특정 자원에 접근하고자할 때 발생하는 예외를 처리 + JWT토큰 예외 처리

@Component
@Slf4j
public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {


	private final ObjectMapper objectMapper;

	@Autowired
	public JWTAuthenticationEntryPoint(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}


	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {

		log.error("Not Authenticated Request", authException);
		log.error("Request Uri : {}", request.getRequestURI());

		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setStatus(HttpStatus.UNAUTHORIZED.value());
		response.setCharacterEncoding("UTF-8");

		ApiResponse<Object> apiResponse = ApiResponse.createError(authException.getMessage());
		String responseBody = objectMapper.writeValueAsString(apiResponse);
		response.getWriter().write(responseBody);
	}
}
