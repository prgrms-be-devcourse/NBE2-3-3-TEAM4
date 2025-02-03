package com.nbe2_3_3_team4.backend.domain.order.repository

import com.nbe2_3_3_team4.backend.domain.member.entity.Member
import com.nbe2_3_3_team4.backend.domain.order.entity.Order
import com.nbe2_3_3_team4.backend.domain.order.entity.enums.OrderStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDateTime
import java.util.Optional

interface OrderRepository : JpaRepository<Order, String> {
    fun findByPaymentKey(paymentKey: String): Order?
    fun findAllByMember(member: Member): Optional<List<Order>>

    fun findByOrderStatusAndOrderDetailCarNumber(
        orderStatus: OrderStatus,
        carNumber: String
    ): Optional<Order>

    fun findAllByOrderStatusInAndOrderDetailCarNumber(
        orderStatus: List<OrderStatus>,
        carNumber: String
    ): List<Order>

    fun findAllByCreatedAtBeforeAndOrderStatus(
        createdAt: LocalDateTime,
        orderStatus: OrderStatus
    ): List<Order>

    fun findAllByMemberAndOrderStatusNotIn(
        member: Member,
        statuses: List<OrderStatus>
    ): Optional<List<Order>>

}