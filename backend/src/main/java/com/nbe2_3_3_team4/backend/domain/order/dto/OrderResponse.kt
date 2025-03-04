package com.nbe2_3_3_team4.backend.domain.order.dto

import com.nbe2_3_3_team4.backend.domain.order.entity.Order
import com.nbe2_3_3_team4.backend.domain.order.entity.OrderDetail
import com.nbe2_3_3_team4.backend.domain.parking.entity.Parking
import com.nbe2_3_3_team4.backend.domain.ticket.entity.Ticket
import com.nbe2_3_3_team4.backend.domain.order.entity.enums.OrderStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class OrderResponse(
    val parking: String?,
    val addr: String?,
    val carNum: String,
    val startTime: String?,
    val endTime: String?,
    val pkDuration: Int?,
    val price: Int?,
    val addPkDuration: Int,
    val addPrice: Int,
    val totalPrice: Int,
    val paymentDate: LocalDateTime?,
    val status: String
) {
    companion object {
        fun from(parking: Parking, orderDetail: OrderDetail, ticket: Ticket, addPkDuration: Int, addPrice: Int, paymentDate: LocalDateTime?, orderStatus: String): OrderResponse {
            val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 a h시 mm분")
            val start = orderDetail.startParkingTime?.format(formatter)
            val end = orderDetail.endParkingTime?.format(formatter)

            return OrderResponse(
                parking.name,
                parking.address,
                orderDetail.carNumber,
                start,
                end,
                ticket.parkingDuration,
                ticket.price,
                addPkDuration,
                addPrice,
                ticket.price!! + addPrice,
                paymentDate,
                orderStatus
            )
        }
    }

    data class getOrderForPayment(
        val pkDuration: Int?,
        val price: Int?,
    ) {
        companion object {
            fun from (pkDuration: Int?, price: Int?) = getOrderForPayment(pkDuration, price)
        }
    }


    data class CreateOrder(
        val orderId: String,
        val ticketId: Long,
        val memberId: Long,
        val carNum: String
    ) {
        companion object {
            fun from(orderId: String, ticketId: Long, memberId: Long, carNum: String): CreateOrder {
                return CreateOrder(orderId, ticketId, memberId, carNum)
            }
        }
    }

    data class GetOrderHistory(
        val orderId: String,
        val parkingId: Long,
        val carNum: String,
        val status: String,
        val startTime: String?,
        val endTime: String?,
        val duration: Int,
        val price: Int
    ) {
        companion object {
            fun from(order: Order, parking: Parking, ticket: Ticket): GetOrderHistory {
                val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 a h시 mm분")
                val status = when (order.orderStatus) {
                    OrderStatus.WAITING -> "주차 대기"
                    OrderStatus.PARKING -> "주차중"
                    OrderStatus.CANCELED -> "환불"
                    else -> "주차 완료"
                }

                val startTime = order.orderDetail.startParkingTime?.format(formatter)
                val endTime = order.orderDetail.endParkingTime?.format(formatter)

                return GetOrderHistory(
                    order.id,
                    parking.parkingId!!,
                    order.orderDetail.carNumber,
                    status,
                    startTime,
                    endTime,
                    ticket.parkingDuration!!,
                    ticket.price!!
                )
            }
        }
    }
}
