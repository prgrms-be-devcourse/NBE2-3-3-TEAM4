package com.nbe2_3_3_team4.backend.controller;

import com.nbe2_3_3_team4.backend.domain.kakao.dto.KakaoResponse;
import com.nbe2_3_3_team4.backend.domain.kakao.service.KakaoService;
import com.nbe2_3_3_team4.backend.domain.member.dto.MemberRequest;
import com.nbe2_3_3_team4.backend.domain.member.dto.MemberResponse;
import com.nbe2_3_3_team4.backend.domain.member.service.MemberService;
import com.nbe2_3_3_team4.backend.global.response.ApiResponse;
import com.nbe2_3_3_team4.backend.security.dto.TokenRequest;
import com.nbe2_3_3_team4.backend.security.dto.TokenResponse;
import com.nbe2_3_3_team4.backend.security.service.CustomUserDetailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "😎 Auth", description = "사용자 인증 인가 관련 API - 별도의 인증 없이 접근 가능")
public class AuthController {

	private final MemberService memberService;
	private final CustomUserDetailService userDetailService;
	private final KakaoService kaKaoService;

	@Operation(summary = "회원 회원가입 API", description = "회원 가입을 진행합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공")
	})
	@PostMapping("/signup")
	public ResponseEntity<ApiResponse<MemberResponse.Join>> joinMember(
		@RequestBody MemberRequest.Join dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(
			ApiResponse.createSuccess(memberService.join(dto)));
	}

	@Operation(summary = "관리자 회원가입 API", description = "관리자 회원 가입을 진행합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공")
	})
	@PostMapping("/signup/admin")
	public ResponseEntity<ApiResponse<MemberResponse.Join>> joinAdmin(
		@RequestBody MemberRequest.Join dto) {
		return ResponseEntity.status(HttpStatus.CREATED).body(
			ApiResponse.createSuccess(memberService.joinAdmin(dto)));
	}

	@Operation(summary = "로그인 API", description = "회원 로그인 진행하고 토큰을 발급합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공")
	})
	@PostMapping("/login")
	public ResponseEntity<ApiResponse<TokenResponse.Create>> login(
		@RequestBody MemberRequest.Login dto) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.createSuccess(userDetailService.login(dto)));
	}

	@Operation(summary = "토큰 재발급 API", description = "리프레쉬 토큰 을 통해 엑세스 토큰, 리프레쉬 토큰 모두 재발급합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공")
	})
	@PostMapping("/reissue")
	public ResponseEntity<ApiResponse<TokenResponse.Create>> updateTokens(
		@RequestBody TokenRequest.Update dto) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.createSuccess(userDetailService.updateToken(dto.refreshToken())));
	}

	@Operation(summary = "카카오 키워드 검색 API", description = "키워드를 통해 장소를 검색합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공")
	})
	@GetMapping("/kakao/search")
	public ResponseEntity<ApiResponse<List<KakaoResponse.Place>>> searchPlace(
		@Parameter(description = "검색 키워드") @RequestParam(value = "keyword") String keyword,
		@Parameter(description = "한 페이지 당 항목 개수(최대 45개)") @RequestParam(value = "size") int size) {
		return ResponseEntity.ok()
			.body(ApiResponse.createSuccess(kaKaoService.searchPlace(keyword, size)));
	}
}
