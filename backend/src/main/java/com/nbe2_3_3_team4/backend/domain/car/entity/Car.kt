package com.nbe2_3_3_team4.backend.domain.car.entity

import com.nbe2_3_3_team4.backend.domain.car.dto.CarRequest
import com.nbe2_3_3_team4.backend.global.BaseTime
import jakarta.persistence.*


@Entity
@Table(name = "cars")
class Car() : BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "car_id")
    var id: Long? = null

    @Column(name = "car_number")
    var number: String? = null

    @Column(name = "is_primary")
    var isPrimary: Boolean? = null

    constructor(number: String, isPrimary: Boolean) : this() {
        this.isPrimary = isPrimary
        this.number = number
    }

    fun updateIsPrimary(isPrimary: Boolean?) {
        if(isPrimary==true){this.isPrimary = false}
        else{this.isPrimary = true}

    }

    fun modify(dto: CarRequest.Modify) {
        this.number = dto.carNumber
        this.isPrimary = dto.isPrimary
    }

    companion object {
        fun to(dto: CarRequest.RegCar): Car {
            return Car(number = dto.carNumber, isPrimary = dto.isPrimary)
        }
    }
}
