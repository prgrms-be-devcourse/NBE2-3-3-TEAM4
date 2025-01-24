package com.nbe2_3_3_team4.backend.domain.car.service;

import com.nbe2_3_3_team4.backend.domain.car.dto.CarRequest;
import com.nbe2_3_3_team4.backend.domain.car.dto.CarResponse;
import com.nbe2_3_3_team4.backend.domain.car.entity.Car;
import com.nbe2_3_3_team4.backend.domain.car.repository.CarRepository;
import com.nbe2_3_3_team4.backend.domain.member.entity.Member;
import com.nbe2_3_3_team4.backend.domain.member.repository.MemberRepository;
import com.nbe2_3_3_team4.backend.global.exception.BadRequestException;
import com.nbe2_3_3_team4.backend.global.exception.DuplicateException;
import com.nbe2_3_3_team4.backend.global.exception.ErrorCode;
import com.nbe2_3_3_team4.backend.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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

		// 회원의 현재 등록된 차량 수 확인
		if (member.getCars().size() >= 3) {
			throw new BadRequestException(ErrorCode.CAR_LIMIT_OVER);
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
	public List<CarResponse.GetCar> getCars(String email) {
		Member member = memberRepository.findByEmail(email)
			.orElseThrow(() -> new NotFoundException(ErrorCode.USER_NOT_FOUND));

		List<CarResponse.GetCar> cars = new ArrayList<>();

		for(Car car : member.getCars()) {
			cars.add(CarResponse.GetCar.from(car));
		}

		return cars;
	}

	@Transactional
	public Void modify(Long carId, CarRequest.modify dto) {
		Car car = carRepository.findById(carId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.CAR_NOT_FOUND));

		car.modify(dto);

		return null;
	}

	@Transactional
	public Void delete(Long carId) {
		Car car = carRepository.findById(carId)
			.orElseThrow(() -> new NotFoundException(ErrorCode.CAR_NOT_FOUND));

		carRepository.delete(car);
		return null;
	}
}
