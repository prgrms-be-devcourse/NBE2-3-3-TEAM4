package com.nbe2_3_3_team4.backend.domain.order.service

import com.nbe2_3_3_team4.backend.domain.car.repository.CarRepository
import com.nbe2_3_3_team4.backend.domain.member.repository.MemberRepository
import com.nbe2_3_3_team4.backend.domain.order.dto.OrderRequest
import com.nbe2_3_3_team4.backend.domain.order.dto.OrderResponse
import com.nbe2_3_3_team4.backend.domain.order.entity.Order
import com.nbe2_3_3_team4.backend.domain.order.entity.OrderDetail
import com.nbe2_3_3_team4.backend.domain.order.entity.enum.OrderStatus
import com.nbe2_3_3_team4.backend.domain.order.entity.enum.PaymentStatus
import com.nbe2_3_3_team4.backend.domain.order.repository.OrderDetailRepository
import com.nbe2_3_3_team4.backend.domain.order.repository.OrderRepository
import com.nbe2_3_3_team4.backend.domain.parking.entity.Parking
import com.nbe2_3_3_team4.backend.domain.parking.entity.ParkingStatus
import com.nbe2_3_3_team4.backend.domain.ticket.repository.TicketRepository
import com.nbe2_3_3_team4.backend.global.exception.BadRequestException
import com.nbe2_3_3_team4.backend.global.exception.ErrorCode
import com.nbe2_3_3_team4.backend.global.exception.NotFoundException
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import java.time.Duration
import java.time.LocalDateTime
import java.util.*


@Service
@Transactional
class OrderService(
    private val orderRepository: OrderRepository,
    private val orderDetailRepository: OrderDetailRepository,
    private val memberRepository: MemberRepository,
    private val ticketRepository: TicketRepository,
    private val carRepository: CarRepository
) {

    private fun getRandomOrderId(): String = UUID.randomUUID().toString()

    fun createOrder(request: OrderRequest.CreateOrder, email: String): OrderResponse.CreateOrder {
        // 회원 조회
        val member = memberRepository.findByEmail(email)
            .orElseThrow { NotFoundException(ErrorCode.TICKET_NOT_FOUND) }

        // 해당 주차권 조회
        val ticket = ticketRepository.findById(request.ticketId)
            .orElseThrow { NotFoundException(ErrorCode.TICKET_NOT_FOUND) }

        // 주차장 조회
        val parking = ticket!!.parking
        val parkingStatus = parking?.parkingStatus

        // 새 주문에 부여할 새로운 UUID 생성
        val newOrderId = getRandomOrderId()

        val newOrder = if (isAvailable(parkingStatus!!)) {
            val orderDetail = OrderDetail.createOrderDetail(ticket.price!!, request.carNumber)
            orderDetailRepository.save(orderDetail)

            val order = Order.createOrder(newOrderId, ticket, member, orderDetail)
            orderRepository.save(order)
            order
        } else {
            throw BadRequestException(ErrorCode.PKLT_FULL)
        }

        // 주차 현황 주차 차량수 추가
        parkingStatus.updateUsedParkingSpace()

        return OrderResponse.CreateOrder.from(
            newOrder.id, ticket.id!!, member.id!!, request.carNumber
        )
    }

    private fun isAvailable(parkingStatus: ParkingStatus): Boolean {
        val totalParkingSpace = parkingStatus.totalParkingSpace
        val usedParkingSpace = parkingStatus.usedParkingSpace
        return (totalParkingSpace - usedParkingSpace) > 0
    }

    fun getOrder(orderId: String): OrderResponse {
        val order = orderRepository.findById(orderId)
            .orElseThrow { NotFoundException(ErrorCode.ORDER_NOT_FOUND) }

        val ticket = order!!.ticket
        val parking = ticket!!.parking!!
        val orderDetail = order.orderDetail

        // 입차 시간 조회
        val startParkingTime = orderDetail.startParkingTime
            ?: throw BadRequestException(ErrorCode.NOT_PARKED)

        // 출차 시간 조회
        val endTime = orderDetail.endParkingTime ?: LocalDateTime.now()
        val minutes = Duration.between(startParkingTime, endTime).toMinutes()

        val basePkDuration = ticket.parkingDuration!! * 60
        val addPkDuration = (minutes - basePkDuration).toInt()

        return if (addPkDuration <= 0) {
            OrderResponse.from(parking, orderDetail, ticket, 0, 0)
        } else {
            val addPrice = calculateAdditionalPrice(parking, addPkDuration)
            OrderResponse.from(parking, orderDetail, ticket, addPkDuration, addPrice)
        }
    }

    private fun calculateAdditionalPrice(parking: Parking, addPkDuration: Int): Int {
        val price5Minute = (12.0 * parking.addCharge!! / 5)
        return (addPkDuration / 5 * price5Minute).toInt()
    }

    fun completePay(payId: String): String {
        val order = orderRepository.findByPaymentKey(payId)
            //.orElseThrow { NotFoundException(ErrorCode.ORDER_NOT_FOUND) }
            ?: throw NotFoundException(ErrorCode.ORDER_NOT_FOUND)

        order.updatePaymentStatus(PaymentStatus.COMPLETE)
        return order.id
    }

    fun cancelTicket(email: String, orderId: String): String {
        val order = orderRepository.findById(orderId)
            .orElseThrow { NotFoundException(ErrorCode.ORDER_NOT_FOUND) }

        if (order.member.email != email) {
            throw BadRequestException(ErrorCode.BAD_REQUEST)
        }

        if (order.orderStatus == OrderStatus.WAITING) {
            val refundMessage = if (order.createdAt!!.plusMinutes(10).isAfter(LocalDateTime.now())) {
                order.updateOrderStatus(OrderStatus.CANCELED)
                order.updatePaymentStatus(PaymentStatus.COMPLETE)
                order.ticket.parking!!.parkingStatus!!.decreaseUsedParkingSpace()
                "전액 환불되었습니다."
            } else {
                order.updateOrderStatus(OrderStatus.CANCELED)
                order.updatePaymentStatus(PaymentStatus.COMPLETE)
                order.ticket.parking!!.parkingStatus!!.decreaseUsedParkingSpace()
                "구매 후 10분 이상이 경과되어 이용 금액의 50% 환불되었습니다."
            }
            return refundMessage
        }

        throw BadRequestException(ErrorCode.ORDER_CANCEL_UNAVAILABLE)
    }

    fun deleteOrder(id: String): Void? {
        val order = orderRepository.findByPaymentKey(id)
            ?: throw NotFoundException(ErrorCode.ORDER_NOT_FOUND)

        val parkingStatus = order.ticket.parking!!.parkingStatus
        parkingStatus!!.decreaseUsedParkingSpace()

        orderRepository.delete(order)
        return null
    }

    fun getOrderHistory(email: String): List<OrderResponse.GetOrderHistory> {
        val member = memberRepository.findByEmail(email)
            .orElseThrow { NotFoundException(ErrorCode.USER_NOT_FOUND) }

        val orders = orderRepository.findAllByMember(member)
            .orElseThrow { NotFoundException(ErrorCode.ORDER_NOT_FOUND) }

        return orders.map { order ->
            OrderResponse.GetOrderHistory.from(order, order.ticket.parking!!, order.ticket)
        }
    }
}