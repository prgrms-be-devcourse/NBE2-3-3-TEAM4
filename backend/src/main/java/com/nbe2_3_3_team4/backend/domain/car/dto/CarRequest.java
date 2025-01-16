package com.nbe2_3_3_team4.backend.domain.car.dto;

public record CarRequest() {

	public record RegCar(
		String carNumber,
		boolean isPrimary
	) {

	}
}
