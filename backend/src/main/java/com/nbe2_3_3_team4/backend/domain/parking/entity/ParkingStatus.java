package com.nbe2_3_3_team4.backend.domain.parking.entity;


import com.fasterxml.jackson.databind.JsonNode;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
@Table(name = "parking_status")
public class ParkingStatus {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long parkingStatusId;

	private int totalParkingSpace;          // 총 주차 면
	private int usedParkingSpace;           // 사용중인 주차 면

	private LocalDateTime space_updated_at; // 사용 주차면 업데이트 시간

	public static ParkingStatus to(JsonNode data) {
		return ParkingStatus.builder()
			.totalParkingSpace(data.get("tpkct").asInt())
			.usedParkingSpace(data.get("now_prk_vhcl_cnt").asInt())
			.space_updated_at(LocalDateTime.now())
			.build();
	}

	public void ModifyTotalParkingSpaceOfJson() {
		this.totalParkingSpace += 1;
	}
}
