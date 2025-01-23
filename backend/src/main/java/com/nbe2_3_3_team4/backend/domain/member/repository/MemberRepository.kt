package com.nbe2_3_3_team4.backend.domain.member.repository;

import com.nbe2_3_3_team4.backend.domain.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

	boolean existsByEmail(String email);
	Optional<Member> findByEmail(String email);
}
