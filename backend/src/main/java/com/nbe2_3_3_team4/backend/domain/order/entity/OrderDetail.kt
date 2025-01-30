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
    val startParkingTime: LocalDateTime? = null,
    @Column(nullable = true)
    val endParkingTime: LocalDateTime? = null,
    @Column(nullable = true)
    val cancelPrice: Int? = null,
    @Column(nullable = true)
    val addPrice: Int? = null,
    val totalPrice: Int
) : BaseTime() {

    companion object {
        fun createOrderDetail(totalPrice: Int,
                              carNumber: String): OrderDetail {
            return OrderDetail(totalPrice = totalPrice, carNumber = carNumber)
        }
    }
}