package com.nbe2_3_3_team4.backend.domain.member.service;

import com.nbe2_3_3_team4.backend.domain.car.dto.CarRequest;
import com.nbe2_3_3_team4.backend.domain.car.dto.CarResponse;
import com.nbe2_3_3_team4.backend.domain.car.repository.CarRepository;
import com.nbe2_3_3_team4.backend.domain.member.dto.MemberRequest;
import com.nbe2_3_3_team4.backend.domain.member.dto.MemberResponse;
import com.nbe2_3_3_team4.backend.domain.car.entity.Car;
import com.nbe2_3_3_team4.backend.domain.member.entity.Member;
import com.nbe2_3_3_team4.backend.domain.member.entity.enums.Role;
import com.nbe2_3_3_team4.backend.domain.member.repository.MemberRepository;
import com.nbe2_3_3_team4.backend.global.exception.DuplicateException;
import com.nbe2_3_3_team4.backend.global.exception.ErrorCode;
import com.nbe2_3_3_team4.backend.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;
	private final CarRepository carRepository;
	private final PasswordEncoder passwordEncoder;

	@Transactional
	public MemberResponse.Join join(MemberRequest.Join request) {
		if (memberRepository.existsByEmail(request.email())) {
			throw new DuplicateException(ErrorCode.USER_ALREADY_EXIST);
		}
		return MemberResponse.Join.from(
			memberRepository.save(Member.to(request, passwordEncoder, Role.ROLE_USER)));
	}

	@Transactional
	public MemberResponse.Join joinAdmin(MemberRequest.Join dto) {
		if (memberRepository.existsByEmail(dto.email())) {
			throw new DuplicateException(ErrorCode.ADMIN_ALREADY_EXIST);
		}

		return MemberResponse.Join.from(
			memberRepository.save(Member.to(dto, passwordEncoder, Role.ROLE_ADMIN)));
	}

	@Transactional
	public Void modifyInfo(MemberRequest.Modify dto, String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

		member.updateMember(dto.name(), dto.contact());

		return null;
	}

	@Transactional(readOnly = true)
	public Void verify(String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

		return null;
	}

	@Transactional(readOnly = true)
	public MemberResponse.GetMember getMember(String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

		return MemberResponse.GetMember.from(member);
	}
}
