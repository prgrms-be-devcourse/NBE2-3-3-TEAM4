package com.nbe2_3_3_team4.backend.domain.ticket.dto

import com.nbe2_3_3_team4.backend.domain.order.entity.Order
import com.nbe2_3_3_team4.backend.domain.order.entity.enums.OrderStatus
import com.nbe2_3_3_team4.backend.domain.ticket.entity.Ticket
import java.time.Duration
import java.time.format.DateTimeFormatter

class TicketResponse {

    data class GetTicket(
        val pkDuration: Int?,
        val price: Int?
    ) {
        companion object {
            fun from(ticket: Ticket): GetTicket {
                return GetTicket(ticket.parkingDuration, ticket.price)
            }
        }
    }

    data class GetTicketHistory(
        val orderId: String?,
        val parkingName: String?,
        val carNum: String?,
        val price: Int?,
        val orderDay: String?,
        val status: String?
    ) {
        companion object {

            fun from(order: Order) = with(order) {
                val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분")
                val status = when (orderStatus) {
                    OrderStatus.WAITING -> "주차대기"
                    OrderStatus.PARKING -> "주차중"
                    OrderStatus.FINISHED -> "주차종료"
                    OrderStatus.CANCELED -> "환불"
                }
                GetTicketHistory(
                    orderId = id,
                    parkingName = ticket.parking?.name.orEmpty(),
                    carNum = orderDetail.carNumber,
                    price = ticket.price,
                    orderDay = createdAt?.format(formatter),
                    status = status
                )
            }
        }
    }

    data class GetTicketDetail(
        val orderId: String?,
        val parkingName: String?,
        val parkingAddress: String?,
        val carNum: String?,
        val enterTime: String?,
        val exitTime: String?,
        val basicTime: Int?,
        val basicPrice: Int?,
        val addTime: String?,
        val addPrice: Int?,
        val cancelPrice: Int?,
        val totalPrice: Int?
    ) {
        companion object {
            fun from(order: Order) = with(order) {
                val formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초")
                val addTime = orderDetail.endParkingTime?.let { Duration.between(createdAt!!.plusMinutes(ticket.parkingDuration!!.toLong()), it) }
                GetTicketDetail(
                    orderId = id,
                    parkingName = ticket.parking?.name.orEmpty(),
                    parkingAddress = ticket.parking?.address.orEmpty(),
                    carNum = orderDetail.carNumber,
                    enterTime = orderDetail.startParkingTime?.format(formatter),
                    exitTime = orderDetail.endParkingTime?.format(formatter),
                    basicTime = ticket.parking?.basicCharge,
                    basicPrice = ticket.price,
                    addTime = addTime?.let{ it.toHours().takeIf { it != 0L }?.let { "${it}시간 " }.orEmpty() +
                              it.toMinutesPart().takeIf { it != 0 }?.let { "${it}분" }.orEmpty() },
                    addPrice = orderDetail.addPrice,
                    cancelPrice = orderDetail.cancelPrice,
                    totalPrice = orderDetail.totalPrice
                )
            }
        }
    }
}