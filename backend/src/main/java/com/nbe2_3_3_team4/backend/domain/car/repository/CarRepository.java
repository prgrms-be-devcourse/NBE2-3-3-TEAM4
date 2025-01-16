package com.nbe2_3_3_team4.backend.domain.car.repository;

import com.nbe2_3_3_team4.backend.domain.car.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CarRepository extends JpaRepository<Car, Long> {
	boolean existsByNumber(String number);
}
