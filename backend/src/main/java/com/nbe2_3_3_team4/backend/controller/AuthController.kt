package com.nbe2_3_3_team4.backend.controller

import com.nbe2_3_3_team4.backend.domain.kakao.dto.KakaoResponse.Place
import com.nbe2_3_3_team4.backend.domain.kakao.service.KakaoService
import com.nbe2_3_3_team4.backend.domain.member.dto.MemberRequest
import com.nbe2_3_3_team4.backend.domain.member.dto.MemberRequest.Login
import com.nbe2_3_3_team4.backend.domain.member.dto.MemberResponse
import com.nbe2_3_3_team4.backend.domain.member.service.MemberService
import com.nbe2_3_3_team4.backend.global.response.ApiResponse
import com.nbe2_3_3_team4.backend.security.dto.TokenRequest
import com.nbe2_3_3_team4.backend.security.dto.TokenResponse.Create
import com.nbe2_3_3_team4.backend.security.service.CustomUserDetailService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
@Tag(name = "😎 Auth", description = "사용자 인증 인가 관련 API - 별도의 인증 없이 접근 가능")
class AuthController(val memberService: MemberService, val userDetailService: CustomUserDetailService, val kaKaoService: KakaoService) {

    @Operation(summary = "회원 회원가입 API", description = "회원 가입을 진행합니다.")
    @ApiResponses(io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공"))
    @PostMapping("/signup")
    fun joinMember(
            @RequestBody dto: MemberRequest.Join): ResponseEntity<ApiResponse<MemberResponse.Join>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.createSuccess(memberService.join(dto)))
    }

    @Operation(summary = "관리자 회원가입 API", description = "관리자 회원 가입을 진행합니다.")
    @ApiResponses(io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공"))
    @PostMapping("/signup/admin")
    fun joinAdmin(
            @RequestBody dto: MemberRequest.Join): ResponseEntity<ApiResponse<MemberResponse.Join>> {
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.createSuccess(memberService.joinAdmin(dto)))
    }

    @Operation(summary = "로그인 API", description = "회원 로그인 진행하고 토큰을 발급합니다.")
    @ApiResponses(io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    @PostMapping("/login")
    fun login(
            @RequestBody dto: Login): ResponseEntity<ApiResponse<Create>> {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.createSuccess(userDetailService.login(dto)))
    }

    @Operation(summary = "토큰 재발급 API", description = "리프레쉬 토큰 을 통해 엑세스 토큰, 리프레쉬 토큰 모두 재발급합니다.")
    @ApiResponses(io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공"))
    @PostMapping("/reissue")
    fun updateTokens(
            @RequestBody dto: TokenRequest.Update): ResponseEntity<ApiResponse<Create>> {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.createSuccess(userDetailService.updateToken(dto.refreshToken)))
    }

    @Operation(summary = "카카오 키워드 검색 API", description = "키워드를 통해 장소를 검색합니다.")
    @ApiResponses(io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    @GetMapping("/kakao/search")
    fun searchPlace(
            @Parameter(description = "검색 키워드") @RequestParam(value = "keyword") keyword: String,
            @Parameter(description = "한 페이지 당 항목 개수(최대 15개)") @RequestParam(value = "size") size: Int): ResponseEntity<ApiResponse<List<Place>>> {
        return ResponseEntity.ok()
                .body(ApiResponse.createSuccess(kaKaoService.searchPlace(keyword, size)))
    }
}
