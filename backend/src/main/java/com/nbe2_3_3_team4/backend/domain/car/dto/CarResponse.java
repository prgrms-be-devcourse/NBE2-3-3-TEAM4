package com.nbe2_3_3_team4.backend.domain.car.dto;

import com.nbe2_3_3_team4.backend.domain.car.entity.Car;

import java.util.List;

public record CarResponse() {

	public record RegCar(
		Car car
	) {
		public static RegCar from(Car car) {
			return new RegCar(car);
		}
	}

	public record GetCars(
		List<Car> cars
	) {
		public static GetCars from(List<Car> cars) {
			return new GetCars(cars);
		}
	}
}
