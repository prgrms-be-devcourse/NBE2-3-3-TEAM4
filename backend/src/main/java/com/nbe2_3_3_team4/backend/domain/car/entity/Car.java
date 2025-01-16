package com.nbe2_3_3_team4.backend.domain.car.entity;

import com.nbe2_3_3_team4.backend.domain.car.dto.CarRequest;
import com.nbe2_3_3_team4.backend.domain.member.dto.MemberRequest;
import com.nbe2_3_3_team4.backend.global.BaseTime;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Builder
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "cars")
public class Car extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "car_id")
	private Long id;

	@Column(name = "car_number")
	private String number;
	@Column(name = "is_primary")
	private Boolean isPrimary;


	public void updateIsPrimary(Boolean isPrimary) {
		this.isPrimary = isPrimary;
	}

	public static Car to(CarRequest.RegCar dto) {
		return Car.builder()
			.number(dto.carNumber())
			.isPrimary(dto.isPrimary())
			.build();
	}
}
