package com.nbe2_3_3_team4.backend.domain.car.dto

class CarRequest {
    data class RegCar(val carNumber: String,
                      val isPrimary: Boolean
    )

    data class Modify(val carNumber: String, val isPrimary: Boolean)

}
