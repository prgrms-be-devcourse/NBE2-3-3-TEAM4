package com.nbe2_3_3_team4.backend.domain.member.entity;

import com.nbe2_3_3_team4.backend.domain.car.entity.Car;
import com.nbe2_3_3_team4.backend.domain.member.dto.MemberRequest;
import com.nbe2_3_3_team4.backend.domain.member.entity.enums.LoginType;
import com.nbe2_3_3_team4.backend.domain.member.entity.enums.Role;
import com.nbe2_3_3_team4.backend.global.BaseTime;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "members")
public class Member extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "member_id")
	private Long id;

	@Column(name = "name")
	private String name;
	@Column(name = "email")
	private String email;
	@Column(name = "password")
	private String password;
	@Column(name = "contact")
	private String contact;
	@Column(name = "role")
	@Enumerated(EnumType.STRING)
	private Role role;
	@Column(name = "social_id")
	private Long socialId;
	@Column(name = "login_type")
	@Enumerated(EnumType.STRING)
	private LoginType loginType;

	@OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Car> cars = new ArrayList<>();

	public static Member to(MemberRequest.Join dto, PasswordEncoder passwordEncoder, Role role) {
		return Member.builder()
			.email(dto.email())
			.name(dto.name())
			.password(passwordEncoder.encode(dto.password()))
			.contact(dto.contact())
			.role(role)
			.loginType(LoginType.BASIC)
			.build();
	}
	public void updateMember(String name, String contact) {
		this.name = name;
		this.contact = contact;
	}
	public Car addCar(Car car) {
		cars.add(car);
		return car;
	}
}
