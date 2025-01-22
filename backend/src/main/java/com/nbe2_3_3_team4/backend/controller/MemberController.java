package com.nbe2_3_3_team4.backend.controller;


import com.nbe2_3_3_team4.backend.domain.car.dto.CarRequest;
import com.nbe2_3_3_team4.backend.domain.car.dto.CarResponse;
import com.nbe2_3_3_team4.backend.domain.member.dto.MemberRequest;
import com.nbe2_3_3_team4.backend.domain.member.dto.MemberResponse;
import com.nbe2_3_3_team4.backend.domain.member.service.MemberService;
import com.nbe2_3_3_team4.backend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Tag(name = "😏 Member", description = "회원 관련 API")
public class MemberController {

	private final MemberService memberService;

	@Operation(summary = "회원 정보 수정 API", description = "회원 정보를 수정합니다.")
	@ApiResponses({@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공")})
	@PutMapping
	public ResponseEntity<ApiResponse<Void>> modifyMember(
		@AuthenticationPrincipal User user, @RequestBody MemberRequest.Modify dto) {
		return ResponseEntity.ok()
			.body(ApiResponse.createSuccess(memberService.modifyInfo(dto, user.getUsername())));
	}

	@Operation(summary = "회원 인증 API", description = "회원 유무를 인증합니다.")
	@ApiResponses({@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공")})
	@GetMapping("/verify")
	public ResponseEntity<ApiResponse<Void>> verify(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok()
			.body(ApiResponse.createSuccess(memberService.verify(user.getUsername())));
	}

	@Operation(summary = "회원 정보 조회 API", description = "회원 정보를 조회합니다.")
	@ApiResponses({
		@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공")
	})
	@GetMapping
	public ResponseEntity<ApiResponse<MemberResponse.GetMember>> getMemberInfo(@AuthenticationPrincipal User user) {
		return ResponseEntity.ok()
			.body(ApiResponse.createSuccess(memberService.getMember(user.getUsername())));
	}

}
