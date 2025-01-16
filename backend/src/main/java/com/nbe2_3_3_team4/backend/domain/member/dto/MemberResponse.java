package com.nbe2_3_3_team4.backend.domain.member.dto;

import com.nbe2_3_3_team4.backend.domain.car.entity.Car;
import com.nbe2_3_3_team4.backend.domain.member.entity.Member;

public record MemberResponse() {

	public record Join(
		Member member
	) {
		public static Join from(Member member) {
			return new Join(member);
		}
	}

}
