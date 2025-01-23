package com.nbe2_3_3_team4.backend.domain.member.dto;

import io.swagger.v3.oas.annotations.Parameter;

public record MemberRequest() {

	public record Join(
		String name,
		String email,
		String password,
		String contact
	) {

	}

	public record Login(
		@Parameter(description = "email")
		String email,
		@Parameter(description = "비밀번호")
		String password
	) {

	}

	public record Modify(
		String contact,
		String name) {

	}


}
