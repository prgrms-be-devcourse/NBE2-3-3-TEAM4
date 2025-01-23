package com.nbe2_3_3_team4.backend.domain.parking.repository

import com.nbe2_3_3_team4.backend.domain.parking.entity.Parking
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ParkingRepository : JpaRepository<Parking?, Long?> {
    fun findByName(parkingName: String?): Parking?
}
