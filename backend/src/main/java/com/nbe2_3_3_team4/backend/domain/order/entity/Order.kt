package com.nbe2_3_3_team4.backend.domain.order.entity

import com.nbe2_3_3_team4.backend.domain.member.entity.Member
import com.nbe2_3_3_team4.backend.domain.order.entity.enum.OrderStatus
import com.nbe2_3_3_team4.backend.domain.order.entity.enum.PaymentStatus
import com.nbe2_3_3_team4.backend.domain.ticket.entity.Ticket
import com.nbe2_3_3_team4.backend.global.BaseTime
import jakarta.persistence.*

@Entity
@Table(name = "orders")
open class Order(
    @Id
    @Column(name = "order_id")
    val id: String,

    @Enumerated(EnumType.STRING)
    var orderStatus: OrderStatus = OrderStatus.WAITING,

    @Enumerated(EnumType.STRING)
    var paymentStatus: PaymentStatus = PaymentStatus.WAITING,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    val ticket: Ticket,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE], orphanRemoval = true)
    @JoinColumn(name = "order_detail_id")
    val orderDetail: OrderDetail,

    @Column(nullable = true)
    val paymentDate: String? = null,
    @Column(nullable = true)
    val paymentKey: String? = null
) : BaseTime() {

    fun updatePaymentStatus(paymentStatus: PaymentStatus) {
        this.paymentStatus = paymentStatus
    }

    fun updateOrderStatus(orderStatus: OrderStatus) {
        this.orderStatus = orderStatus
    }

    companion object {
        @JvmStatic
        fun createOrder(
            id: String,
            ticket: Ticket,
            member: Member,
            orderDetail: OrderDetail
        ): Order {
            return Order(
                id = id,
                ticket = ticket,
                member = member,
                orderDetail = orderDetail
            )
        }
    }
}
