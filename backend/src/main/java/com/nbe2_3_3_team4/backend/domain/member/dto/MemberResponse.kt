package com.nbe2_3_3_team4.backend.domain.member.dto;

import com.nbe2_3_3_team4.backend.domain.member.entity.Member;
import com.nbe2_3_3_team4.backend.domain.member.entity.enums.LoginType;
import com.nbe2_3_3_team4.backend.domain.member.entity.enums.Role;

public record MemberResponse() {

    public record Join(
        Member member
    ) {
        public static Join from(Member member) {
            return new Join(member);
        }
    }

    public record GetMember(
        String name,
        String email,
        String contact,
        Role role,
        LoginType loginType
    ) {
        public static GetMember from(Member member) {
            return new GetMember(member.getName(), member.getEmail(), member.getContact(), member.getRole(), member.getLoginType());
        }
    }
}
