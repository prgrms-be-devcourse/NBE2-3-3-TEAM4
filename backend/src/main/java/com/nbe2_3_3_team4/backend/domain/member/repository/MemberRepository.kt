package com.nbe2_3_3_team4.backend.domain.member.repository

import com.nbe2_3_3_team4.backend.domain.member.entity.Member
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

interface MemberRepository : JpaRepository<Member?, Long?> {
    fun existsByEmail(email: String?): Boolean
    fun findByEmail(email: String?): Optional<Member>
}
