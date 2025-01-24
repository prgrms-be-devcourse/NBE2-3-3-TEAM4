package com.nbe2_3_3_team4.backend.domain.car.dto

class CarRequest {
    data class RegCar(val carNumber: String)

    data class Modify(val carNumber: String)

}
