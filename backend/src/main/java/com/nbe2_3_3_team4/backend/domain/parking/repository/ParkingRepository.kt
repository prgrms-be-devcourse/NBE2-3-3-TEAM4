package com.nbe2_3_3_team4.backend.domain.parking.repository;

import com.nbe2_3_3_team4.backend.domain.parking.entity.Parking;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingRepository extends JpaRepository<Parking, Long> {

	Parking findByName(String parkingName);

}
