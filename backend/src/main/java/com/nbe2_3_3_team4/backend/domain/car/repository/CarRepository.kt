package com.nbe2_3_3_team4.backend.domain.car.repository

import com.nbe2_3_3_team4.backend.domain.car.entity.Car
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface CarRepository : JpaRepository<Car?, Long?> {
    fun existsByNumber(number: String?): Boolean
}
