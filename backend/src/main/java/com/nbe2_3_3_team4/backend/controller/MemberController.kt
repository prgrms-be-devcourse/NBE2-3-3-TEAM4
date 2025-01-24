package com.nbe2_3_3_team4.backend.controller

import com.nbe2_3_3_team4.backend.domain.member.dto.MemberRequest.Modify
import com.nbe2_3_3_team4.backend.domain.member.dto.MemberResponse.GetMember
import com.nbe2_3_3_team4.backend.domain.member.service.MemberService
import com.nbe2_3_3_team4.backend.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/members")
@Tag(name = "😏 Member", description = "회원 관련 API")
class MemberController(val memberService: MemberService) {

    @Operation(summary = "회원 정보 수정 API", description = "회원 정보를 수정합니다.")
    @ApiResponses(io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공"))
    @PutMapping
    fun modifyMember(
            @AuthenticationPrincipal user: User, @RequestBody dto: Modify): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok()
                .body(ApiResponse.createSuccess(memberService.modifyInfo(dto, user.username)))
    }

    @Operation(summary = "회원 인증 API", description = "회원 유무를 인증합니다.")
    @ApiResponses(io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    @GetMapping("/verify")
    fun verify(@AuthenticationPrincipal user: User): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok()
                .body(ApiResponse.createSuccess(memberService.verify(user.username)))
    }

    @Operation(summary = "회원 정보 조회 API", description = "회원 정보를 조회합니다.")
    @ApiResponses(io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    @GetMapping
    fun getMemberInfo(@AuthenticationPrincipal user: User): ResponseEntity<ApiResponse<GetMember>> {
        return ResponseEntity.ok()
                .body(ApiResponse.createSuccess(memberService.getMember(user.username)))
    }
}
