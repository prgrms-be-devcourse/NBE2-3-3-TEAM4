package com.nbe2_3_3_team4.backend.domain.order.dto

import com.nbe2_3_3_team4.backend.domain.order.entity.enum.PaymentStatus

data class OrderRequest(
    val createOrder: CreateOrder
) {
    data class CreateOrder(
        val paymentId: String,
        val ticketId: Long,
        val carNumber: String,
        val paymentStatus: PaymentStatus
    )
}
