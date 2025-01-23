package com.nbe2_3_3_team4.backend.domain.parking.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.nbe2_3_3_team4.backend.domain.ticket.entity.Ticket;
import com.nbe2_3_3_team4.backend.global.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Builder
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "parking")
public class Parking extends BaseTime {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "parking_id")
	private Long parkingId;

	@Column(name = "name")
	private String name;
	@Column(name = "address")
	private String address;
	@Column(name = "latitude")
	private Double latitude;
	@Column(name = "longitude")
	private Double longitude;
	@Column(name = "we_open_time", length = 4)
	private String weOpenTime;
	@Column(name = "we_end_time", length = 4)
	private String weEndTime;
	@Column(name = "wd_open_time", length = 4)
	private String wdOpenTime;
	@Column(name = "wd_end_time", length = 4)
	private String wdEndTime;
	@Column(name = "basic_charge")
	private int basicCharge;
	@Column(name = "basic_charge_time")
	private int basicChargeTime;
	@Column(name = "add_charge")
	private int addCharge;
	@Column(name = "add_charge_time")
	private int addChargeTime;

	@OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "pklt_status_id")
	private ParkingStatus parkingStatus;

	@OneToMany(mappedBy = "parking", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Ticket> tickets = new ArrayList<>();

	public static Parking to(JsonNode data, ParkingStatus status) {
		int addCharge = data.get("add_prk_crg").asInt();
		int addChargeTime = data.get("add_prk_hr").asInt();

		if (data.get("add_prk_crg").asInt() == 0) {
			addCharge = 220;
			addChargeTime = 5;
		}

		return Parking.builder()
			.name(data.get("pklt_nm").asText())
			.address(data.get("addr").asText())
			.latitude(data.get("lat").asDouble())
			.longitude(data.get("lot").asDouble())
			.weOpenTime(data.get("we_oper_bgng_tm").asText())
			.weEndTime(data.get("we_oper_end_tm").asText())
			.wdOpenTime(data.get("wd_oper_bgng_tm").asText())
			.wdEndTime(data.get("wd_oper_end_tm").asText())
			.basicCharge(data.get("bsc_prk_crg").asInt())
			.basicChargeTime(data.get("bsc_prk_hr").asInt())
			.addCharge(addCharge)
			.addChargeTime(addChargeTime)
			.parkingStatus(status)
			.build();
	}

	public void regTicket(Ticket ticket) {
		this.tickets.add(ticket);
	}

}
