package com.nbe2_3_3_team4.backend.domain.ticket.entity;

import com.nbe2_3_3_team4.backend.domain.parking.entity.Parking;
import com.nbe2_3_3_team4.backend.global.BaseTime;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "tickets")
public class Ticket extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "price")
	private int price;

	@Column(name = "parking_duration")
	private int parkingDuration;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parking_id")
	private Parking parking;

	public static Ticket to(Parking parking, int price, int parkingDuration) {
		return Ticket.builder()
			.parking(parking)
			.price(price)
			.parkingDuration(parkingDuration)
			.build();
	}
}
