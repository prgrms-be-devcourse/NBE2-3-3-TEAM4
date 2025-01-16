package com.nbe2_3_3_team4.backend.domain.car.service;

import com.nbe2_3_3_team4.backend.domain.car.dto.CarRequest;
import com.nbe2_3_3_team4.backend.domain.car.dto.CarResponse;
import com.nbe2_3_3_team4.backend.domain.car.entity.Car;
import com.nbe2_3_3_team4.backend.domain.car.repository.CarRepository;
import com.nbe2_3_3_team4.backend.domain.member.entity.Member;
import com.nbe2_3_3_team4.backend.domain.member.repository.MemberRepository;
import com.nbe2_3_3_team4.backend.global.exception.DuplicateException;
import com.nbe2_3_3_team4.backend.global.exception.ErrorCode;
import com.nbe2_3_3_team4.backend.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {

	private final CarRepository carRepository;
	private final MemberRepository memberRepository;

	@Transactional
	public CarResponse.RegCar registerCar(CarRequest.RegCar dto, String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));
		if (carRepository.existsByNumber(dto.carNumber())) {
			throw new DuplicateException(ErrorCode.CAR_ALREADY_EXISTS);
		}
		return CarResponse.RegCar.from(member.addCar(Car.to(dto)));
	}

	@Transactional
	public Void updatePrimary(Long carId) {
		Car car = carRepository.findById(carId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.CAR_NOT_FOUND));

		car.updateIsPrimary(!car.getIsPrimary());

		return null;
	}

	@Transactional(readOnly = true)
	public CarResponse.GetCars getCars(String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

		return CarResponse.GetCars.from(member.getCars());
	}
}
