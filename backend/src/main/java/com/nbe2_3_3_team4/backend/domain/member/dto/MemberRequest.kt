package com.nbe2_3_3_team4.backend.domain.member.dto

import io.swagger.v3.oas.annotations.Parameter

class MemberRequest {
    class Join(val name: String, val email: String, val password: String, val contact: String
    )
    class Login(@Parameter(description = "email") var email: String,
                @Parameter(description = "비밀번호") var password: String
    )

    class  Modify(var contact: String,
                 val name: String)
}
