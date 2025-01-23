package com.nbe2_3_3_team4.backend.domain.member.service

import com.nbe2_3_3_team4.backend.domain.car.repository.CarRepository
import com.nbe2_3_3_team4.backend.domain.member.dto.MemberRequest
import com.nbe2_3_3_team4.backend.domain.member.dto.MemberRequest.Modify
import com.nbe2_3_3_team4.backend.domain.member.dto.MemberResponse
import com.nbe2_3_3_team4.backend.domain.member.dto.MemberResponse.GetMember
import com.nbe2_3_3_team4.backend.domain.member.entity.Member.Companion.to
import com.nbe2_3_3_team4.backend.domain.member.entity.enums.Role
import com.nbe2_3_3_team4.backend.domain.member.repository.MemberRepository
import com.nbe2_3_3_team4.backend.global.exception.DuplicateException
import com.nbe2_3_3_team4.backend.global.exception.ErrorCode
import com.nbe2_3_3_team4.backend.global.exception.NotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional


//
//open -> 클래스나 메서드를 상속(재정의) 가능하게 만들 때 사용됩니다.
// Kotlin의 기본적인 특징은 "모든 클래스와 메서드가 기본적으로 final"이라는 것입니다.
// 즉, Java에서는 기본적으로 클래스와 메서드를 상속할 수 있지만, Kotlin에서는 open을 명시적으로 선언해야만 상속 가능합니다.
// @Transactional 이 명시된 메소드를 실행시키기 위해  kotlin은 프록시 객체를 생성하는데, 이 때 open이 없으면 매소드를 재정의 불가능. 따라서 open 키워드를 달아줘야 함.
@Service
class MemberService (private val memberRepository: MemberRepository, private val carRepository: CarRepository, private val passwordEncoder: PasswordEncoder){
    @Transactional
    fun join(request: MemberRequest.Join): MemberResponse.Join {

        if (memberRepository.existsByEmail(request.email)) {
            throw DuplicateException(ErrorCode.USER_ALREADY_EXIST)
        }
        return MemberResponse.Join.from(
                memberRepository.save(to(request, passwordEncoder, Role.ROLE_USER)))
    }

    @Transactional
    fun joinAdmin(dto: MemberRequest.Join): MemberResponse.Join {
        if (memberRepository.existsByEmail(dto.email)) {
            throw DuplicateException(ErrorCode.ADMIN_ALREADY_EXIST)
        }

        return MemberResponse.Join.from(
                memberRepository.save(to(dto, passwordEncoder, Role.ROLE_ADMIN)))
    }

    @Transactional
    fun modifyInfo(dto: Modify, email: String?) {

        val member = memberRepository.findByEmail(email)?.orElse(null) ?: throw NotFoundException(ErrorCode.USER_NOT_FOUND)

        member.updateMember(dto.name, dto.contact)
    }

    @Transactional(readOnly = true)
    fun verify(email: String?) {

        memberRepository.findByEmail(email) ?: throw NotFoundException(ErrorCode.USER_NOT_FOUND)
    }

    @Transactional(readOnly = true)
    fun getMember(email: String?): GetMember {
        val member = memberRepository.findByEmail(email)?.orElse(null) ?: throw NotFoundException(ErrorCode.USER_NOT_FOUND)
        return GetMember.from(member)
    }
}
