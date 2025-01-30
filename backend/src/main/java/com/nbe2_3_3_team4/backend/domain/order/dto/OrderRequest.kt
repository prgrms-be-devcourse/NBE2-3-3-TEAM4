package com.nbe2_3_3_team4.backend.domain.order.dto


data class OrderRequest(
    val createOrder: CreateOrder
) {
    data class CreateOrder(
        val ticketId: Long,
        val carNumber: String,
    )
}