package com.nbe2_3_3_team4.backend.domain.order.entity

import com.nbe2_3_3_team4.backend.domain.member.entity.Member
import com.nbe2_3_3_team4.backend.domain.order.entity.enums.OrderStatus
import com.nbe2_3_3_team4.backend.domain.order.entity.enums.PaymentStatus
import com.nbe2_3_3_team4.backend.domain.ticket.entity.Ticket
import com.nbe2_3_3_team4.backend.global.BaseTime
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "orders")
class Order(
    @Id
    @Column(name = "order_id")
    var id: String,

    @Enumerated(EnumType.STRING)
    var orderStatus: OrderStatus = OrderStatus.WAITING,

    @Enumerated(EnumType.STRING)
    var paymentStatus: PaymentStatus = PaymentStatus.WAITING,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    var ticket: Ticket,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    var member: Member,

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE], orphanRemoval = true)
    @JoinColumn(name = "order_detail_id")
    var orderDetail: OrderDetail,

    @Column(nullable = true)
    var paymentDate: LocalDateTime? = null,
    @Column(nullable = true)
    var paymentKey: String? = null
) : BaseTime() {

    fun updatePaymentStatus(paymentStatus: PaymentStatus) {
        this.paymentStatus = paymentStatus
    }

    fun updateOrderStatus(orderStatus: OrderStatus) {
        this.orderStatus = orderStatus
    }

    fun updatePaymentInfo(paymentKey: String?, paymentDate: LocalDateTime?) {
        this.paymentKey = paymentKey
        this.paymentDate = paymentDate
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