package com.nbe2_3_3_team4.backend.security.service;

import com.nbe2_3_3_team4.backend.domain.member.dto.MemberRequest;
import com.nbe2_3_3_team4.backend.domain.member.entity.Member;
import com.nbe2_3_3_team4.backend.domain.member.entity.enums.Role;
import com.nbe2_3_3_team4.backend.domain.member.repository.MemberRepository;
import com.nbe2_3_3_team4.backend.global.exception.ErrorCode;
import com.nbe2_3_3_team4.backend.global.exception.JWTCustomException;
import com.nbe2_3_3_team4.backend.global.exception.NotFoundException;
import com.nbe2_3_3_team4.backend.security.dto.TokenResponse;
import com.nbe2_3_3_team4.backend.security.util.TokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * member 관련 인증과 인가 담당하는 서비스 계층
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService {

	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;
	private final TokenProvider tokenProvider;
	private final AuthenticationManagerBuilder authManagerBuilder;

	private static final Map<Role, List<GrantedAuthority>> authorityMap = new HashMap<>();

	@Override
	public UserDetails loadUserByUsername(final String email) throws UsernameNotFoundException {
		return getUserDetails(findUserByEmail(email));
	}

	private UserDetails getUserDetails(Member member) {
		return new User(member.getEmail(), null, authorityMap.get(member.getRole()));
	}

	public Member findUserByEmail(final String userEmail) {
		return memberRepository
			.findByEmail(userEmail)
			.orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
	}

	//accessToken, refreshToken update
	public TokenResponse.Create updateToken(String refreshToken) {

		try {
			String email = tokenProvider.findUser(refreshToken);
			Member member = findUserByEmail(email);

			List<GrantedAuthority> authorities = authorityMap.get(member.getRole());

			User user = new User(email, "password", authorities);
			// Authentication 객체 생성
			Authentication authentication = new UsernamePasswordAuthenticationToken(user, null,
				authorities);

			return issueAllTokens(authentication);
		}
		//refresh token parsing중 에러 발생(유효하지 않은 토큰인 경우) 처리
		catch (ExpiredJwtException e) {
			log.info("refresh token 만료");
			throw new JWTCustomException(ErrorCode.EXPIRED_REFRESH_TOKEN);
		} catch (UnsupportedJwtException | MalformedJwtException | SignatureException |
				 IllegalArgumentException e) {
			log.info("유효하지 않은 refresh token");
			throw new JWTCustomException(ErrorCode.INVALID_REFRESH_TOKEN);
		}
	}


	public TokenResponse.Create issueAllTokens(Authentication authentication) {
		return tokenProvider.createAllToken(authentication);
	}


	public TokenResponse.Create login(MemberRequest.Login dto) {
		String email = dto.email();

		Member member =
			memberRepository.findByEmail(email)
				.orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

		if (passwordEncoder.matches(dto.password(), member.getPassword())) {
			List<GrantedAuthority> authorities = authorityMap.get(member.getRole());

			User user = new User(email, "password", authorities);

			// Authentication 객체 생성
			Authentication authentication = new UsernamePasswordAuthenticationToken(user, null,
				authorities);
			SecurityContextHolder.getContext().setAuthentication(authentication);

			return issueAllTokens(authentication);
		} else {
			throw new NotFoundException(ErrorCode.INVALID_PASSWORD);
		}
	}

	@EventListener(ApplicationReadyEvent.class)
	public void setAuthoritiesMap() {

		List<GrantedAuthority> generalAuthorities = new ArrayList<GrantedAuthority>();
		generalAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));

		List<GrantedAuthority> adminAuthorities = new ArrayList<GrantedAuthority>();
		adminAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
		adminAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

		authorityMap.put(Role.ROLE_USER, generalAuthorities);
		authorityMap.put(Role.ROLE_ADMIN, adminAuthorities);
	}


}
