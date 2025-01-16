package com.nbe2_3_3_team4.backend.domain.parking.dto;

import com.nbe2_3_3_team4.backend.domain.parking.entity.Parking;

public record ParkingResponse() {

	public record GetNearbyParking(
		Long id,
		String name,
		Double latitude,
		Double longitude,
		String status
	) {
		public static GetNearbyParking from(Parking parking, String status) {
			return new GetNearbyParking(parking.getParkingId(),
				parking.getName(), parking.getLatitude(), parking.getLongitude(), status);
		}
	}

	public record GetParking(
		Long id,
		String name,
		String address,
		Double latitude,
		Double longitude
	) {
		public static GetParking from(Parking parking) {
			return new GetParking(parking.getParkingId(), parking.getName(), parking.getAddress(), parking.getLatitude(), parking.getLongitude());
		}
	}
}
