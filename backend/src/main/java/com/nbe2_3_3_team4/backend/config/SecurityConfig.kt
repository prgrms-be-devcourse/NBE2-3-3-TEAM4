package com.nbe2_3_3_team4.backend.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbe2_3_3_team4.backend.security.handler.JWTAccessDeniedHandler;
import com.nbe2_3_3_team4.backend.security.handler.JWTAuthenticationEntryPoint;
import com.nbe2_3_3_team4.backend.security.handler.JWTFilter;
import com.nbe2_3_3_team4.backend.security.handler.JwtFilterExceptionHandler;
import com.nbe2_3_3_team4.backend.security.util.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

	private final JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JWTAccessDeniedHandler jwtAccessDeniedHandler;
	private final TokenProvider tokenProvider;
	private final String[] frontUrl = {
		"/ttukttak_parking/**"
	};
	private final String[] allowedUrl = {
		"/api/kakao/search", "/api/auth/**", "/api/parking/**"
	};

	@Bean
	SecurityFilterChain filterChain(HttpSecurity http, HandlerMappingIntrospector introspector)
		throws Exception {
		return http.csrf(AbstractHttpConfigurer::disable)
			.headers((headerConfig) ->
				headerConfig.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable)
			).cors(cors -> cors.configurationSource(corsConfigurationSource())) // CORS 관련
			.authorizeHttpRequests((authorizeRequests) ->
				authorizeRequests
					.requestMatchers(frontUrl).permitAll()
					.requestMatchers(allowedUrl).permitAll()
					.requestMatchers("/api/admin/**").hasRole("ADMIN")
					.anyRequest().authenticated()
			)
			.sessionManagement((sessionManagement) ->
				sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
			)
			.addFilterBefore(new JWTFilter(tokenProvider),
				UsernamePasswordAuthenticationFilter.class)
			.addFilterBefore(new JwtFilterExceptionHandler(new ObjectMapper()), JWTFilter.class)
			.exceptionHandling((exceptionHandling) ->
				exceptionHandling
					.authenticationEntryPoint(jwtAuthenticationEntryPoint)
					.accessDeniedHandler(jwtAccessDeniedHandler)
			)
			.build();
	}

	@Bean
	public WebSecurityCustomizer webSecurityCustomizer() {
		return (web) -> web.ignoring()
			.requestMatchers(new AntPathRequestMatcher("/favicon.ico"))
			.requestMatchers(new AntPathRequestMatcher("/css/**"))
			.requestMatchers(new AntPathRequestMatcher("/js/**"))
			.requestMatchers(new AntPathRequestMatcher("/image/**"))
			.requestMatchers(new AntPathRequestMatcher("/swagger-ui/**"))
			.requestMatchers(new AntPathRequestMatcher("/test"))
			.requestMatchers(new AntPathRequestMatcher("/v3/api-docs/**"))
			.requestMatchers(new AntPathRequestMatcher("/swagger-ui.html"));
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowedOrigins(List.of("http://localhost:3000")); // frontend url
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		config.setAllowedHeaders(List.of("*"));
		config.setAllowCredentials(true);
		config.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);
		return source;
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
