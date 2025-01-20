package com.nbe2_3_3_team4.backend.domain.parking.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nbe2_3_3_team4.backend.domain.parking.dto.ParkingResponse;
import com.nbe2_3_3_team4.backend.domain.parking.entity.Parking;
import com.nbe2_3_3_team4.backend.domain.parking.entity.ParkingStatus;
import com.nbe2_3_3_team4.backend.domain.parking.repository.ParkingRepository;
import com.nbe2_3_3_team4.backend.domain.ticket.entity.Ticket;
import com.nbe2_3_3_team4.backend.global.exception.ErrorCode;
import com.nbe2_3_3_team4.backend.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;

@Service
@RequiredArgsConstructor
public class ParkingService {

	private final ParkingRepository parkingRepository;

	@Transactional(readOnly = true)
	public ParkingResponse.GetParking getParking(Long parkingId) {
		Parking parking = parkingRepository.findById(parkingId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.PKLT_NOT_FOUND));

		return ParkingResponse.GetParking.from(parking);
	}

	@Transactional(readOnly = true)
	public ParkingResponse.GetParkingStatus getParkingStatus(Long parkingId) {
		Parking parking = parkingRepository.findById(parkingId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.PKLT_NOT_FOUND));

		return ParkingResponse.GetParkingStatus.from(parking.getParkingStatus());
	}

	public List<ParkingResponse.GetNearbyParking> getNearbyParking(Double lat, Double lng) {
		List<Parking> parkingList = getParkingList();
		List<Parking> nearbyParkingList = filterNearbyParking(parkingList, lat, lng);
		return calculateCongestionAndSort(nearbyParkingList);
	}

	public List<Parking> getParkingList() {
		return parkingRepository.findAll();
	}

	public List<Parking> filterNearbyParking(List<Parking> parkingList, Double latitude, Double longitude) {
		double KM = 0.5;
		double lngDifference = KM / 111 / (Math.cos(latitude));

		Predicate<Parking> latFilter = parking ->
			parking.getLatitude() > latitude - (KM / 111)
				&& latitude + (KM / 111) > parking.getLatitude();
		Predicate<Parking> lngFilter = parking ->
			parking.getLongitude() > longitude - lngDifference
				&& parking.getLongitude() < longitude + lngDifference;

		return parkingList.stream()
			.filter(latFilter.and(lngFilter))
			.toList();
	}

	private List<ParkingResponse.GetNearbyParking> calculateCongestionAndSort(List<Parking> parkingList) {
		return parkingList.stream().sorted(
				Comparator.comparingDouble(
					pklt -> {
						ParkingStatus status = pklt.getParkingStatus();
						return (double) status.getUsedParkingSpace() / status.getTotalParkingSpace() * 100;
					}))
			.map(pklt -> {
				ParkingStatus status = pklt.getParkingStatus();
				double percentage = (double) status.getUsedParkingSpace() / status.getTotalParkingSpace() * 100;
				String congestionStatus;
				if (percentage <= 30) {
					congestionStatus = "여유";
				} else if (percentage <= 70) {
					congestionStatus = "보통";
				} else {
					congestionStatus = "혼잡";
				}
				return ParkingResponse.GetNearbyParking.from(pklt, congestionStatus);
			}).toList();
	}

	@Transactional
	public Void loadDefaultDataByJson() throws IOException {
		ObjectMapper objectMapper = new ObjectMapper();

		JsonNode root = objectMapper.readTree(new ClassPathResource("static/data.json").getFile());
		JsonNode dataArray = root.get("DATA");

		for (JsonNode data : dataArray) {
			String parkingName = data.get("pklt_nm").asText();

			Parking parking = parkingRepository.findByName(parkingName);

			if (parking == null) {
				parkingRepository.save(Parking.to(data, ParkingStatus.to(data)));
			} else {
				parking.getParkingStatus().ModifyTotalParkingSpaceOfJson();
			}
		}

		return null;
	}

	@Transactional
	public Void loadDefaultTickets() {
		List<Parking> parkingList = parkingRepository.findAll();
		int[] nums = {1, 2, 4, 6, 12};

		for (Parking parking : parkingList) {

			// 1시간 2시간 4시간 6시간 12시간
			// bsc_prk_crg => 기본 요금
			// bsc_prk_hr => 분단위
			for (int num : nums) {
				int basicCharge = parking.getBasicCharge();
				int basicChargeTime = parking.getBasicChargeTime();

				if (basicCharge == 0) {
					basicCharge = 220;
					basicChargeTime = 5;
				}

				int result = (basicCharge / basicChargeTime) * 60 * num;

				parking.regTicket(Ticket.to(parking, result, num));
			}
		}

		return null;
	}
}
