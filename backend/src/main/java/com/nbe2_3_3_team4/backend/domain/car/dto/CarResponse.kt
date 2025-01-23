package com.nbe2_3_3_team4.backend.domain.car.dto

import com.nbe2_3_3_team4.backend.domain.car.entity.Car

class CarResponse {
    data class RegCar(val car: Car
    ) {
        companion object {
            fun from(car: Car): RegCar {
                return RegCar(car)
            }
        }
    }

    data class GetCar(var id: Long?,
                 var carNumber: String?,
                 var isPrimary: Boolean?)
    {
        companion object {
            fun from(car: Car): GetCar {
                return GetCar(car.id, car.number, car.isPrimary)
            }
        }
    }
}
