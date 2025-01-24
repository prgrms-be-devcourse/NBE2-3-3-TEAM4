package com.nbe2_3_3_team4.backend.domain.member.dto

import com.nbe2_3_3_team4.backend.domain.member.entity.Member
import com.nbe2_3_3_team4.backend.domain.member.entity.enums.LoginType
import com.nbe2_3_3_team4.backend.domain.member.entity.enums.Role

class MemberResponse {
    data class Join(val member: Member
    ) {
        companion object {
            fun from(member: Member): Join {
                return Join(member)
            }
        }
    }

    data class GetMember(var name: String?,
                    var email: String?,
                    var contact: String?,
                    var role: Role?,
                    var loginType: LoginType?
    ) {
        companion object {
            fun from(member: Member): GetMember {
                return GetMember(member.name, member.email, member.contact, member.role, member.loginType)
            }
        }
    }
}
