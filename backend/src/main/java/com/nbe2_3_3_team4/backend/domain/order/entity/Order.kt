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
open class Order(
    id:String,
    status:OrderStatus,
    paymentStatus:PaymentStatus,
    paymentDate: LocalDateTime?,
    paymentKey: String?,
    ticket: Ticket,
    member: Member,
    orderDetail: OrderDetail
) : BaseTime() {
    @Id
    @Column(name = "order_id")
    val id: String = id

    @Enumerated(EnumType.STRING)
    var status: OrderStatus = status
        private set

    @Enumerated(EnumType.STRING)
    var paymentStatus: PaymentStatus = paymentStatus
        private set

    var paymentDate: LocalDateTime? = null
        private set

    var paymentKey: String? = null
        private set

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    val ticket: Ticket = ticket

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    val member: Member = member

    @OneToOne(fetch = FetchType.LAZY, cascade = [CascadeType.REMOVE], orphanRemoval = true)
    @JoinColumn(name = "order_detail_id")
    val orderDetail: OrderDetail = orderDetail

    fun updatePaymentStatus(status: PaymentStatus) {
        this.paymentStatus = status
    }

    fun updatePaymentInfo(paymentKey: String, paymentDate: LocalDateTime) {
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