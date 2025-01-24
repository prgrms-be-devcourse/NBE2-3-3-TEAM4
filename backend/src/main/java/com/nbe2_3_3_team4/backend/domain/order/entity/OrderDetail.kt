package com.nbe2_3_3_team4.backend.domain.order.entity

import com.nbe2_3_3_team4.backend.global.BaseTime
import jakarta.persistence.*
import java.time.LocalDateTime


@Entity
@Table(name = "order_details")
class OrderDetail : BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null

    private val startParkingTime: LocalDateTime? = null
    private val endParkingTime: LocalDateTime? = null
    private val cancelPrice = 0
    private val addPrice = 0
    private val totalPrice = 0
}