package com.nbe2_3_3_team4.backend.domain.order.entity

import com.nbe2_3_3_team4.backend.global.BaseTime
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "order_details")
data class OrderDetail(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,  // 기본값을 null로 설정하여 생성자를 통해 null을 받을 수 있게 함
    val carNumber: String,
    @Column(nullable = true)
    var startParkingTime: LocalDateTime? = null,
    @Column(nullable = true)
    var endParkingTime: LocalDateTime? = null,
    @Column(nullable = true)
    var cancelPrice: Int? = null,
    @Column(nullable = true)
    var addPrice: Int? = null,
    var totalPrice: Int = 0
) : BaseTime() {

    fun updateStartParkingTime(startParkingTime: LocalDateTime) { this.startParkingTime = startParkingTime }
    fun updateEndParkingTime(endParkingTime: LocalDateTime) { this.endParkingTime = endParkingTime }
    fun updateCancelPrice(cancelPrice: Int) { this.cancelPrice = cancelPrice }
    fun updateAddPrice(addPrice: Int) { this.addPrice = addPrice }
    fun updateTotalPrice(totalPrice: Int) { this.totalPrice = totalPrice }

    companion object {
        fun createOrderDetail(carNumber: String): OrderDetail {
            return OrderDetail(carNumber = carNumber)
        }
    }
}