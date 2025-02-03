package com.nbe2_3_3_team4.backend.domain.ticket.service

import com.nbe2_3_3_team4.backend.domain.member.repository.MemberRepository
import com.nbe2_3_3_team4.backend.domain.order.repository.OrderRepository
import com.nbe2_3_3_team4.backend.domain.ticket.dto.TicketResponse
import com.nbe2_3_3_team4.backend.domain.ticket.repository.TicketRepository
import com.nbe2_3_3_team4.backend.global.exception.ErrorCode
import com.nbe2_3_3_team4.backend.global.exception.NotFoundException
import com.nbe2_3_3_team4.backend.domain.order.entity.enums.OrderStatus.*
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TicketService(
    val memberRepository: MemberRepository,
    val orderRepository: OrderRepository,
    val ticketRepository: TicketRepository
) {

    @Transactional(readOnly = true)
    fun getTicket(id: Long): TicketResponse.GetTicket {
        val ticket = ticketRepository.findById(id)
            .orElseThrow{NotFoundException(ErrorCode.TICKET_NOT_FOUND)}

        return TicketResponse.GetTicket.from(ticket)
    }

    @Transactional(readOnly = true)
    fun getTicketHistory(email: String): List<TicketResponse.GetTicketHistory> {
        val member = memberRepository.findByEmail(email).orElseThrow{NotFoundException(ErrorCode.USER_NOT_FOUND)}
        val tickets = orderRepository.findAllByMemberAndOrderStatusNotInOrderByCreatedAtDesc(member, listOf(WAITING, PARKING))
            .orElseThrow{NotFoundException(ErrorCode.ORDER_NOT_FOUND)}
            .map { TicketResponse.GetTicketHistory.from(it) }

        return tickets
    }

    @Transactional(readOnly = true)
    fun getTicketDetail(orderId: String): TicketResponse.GetTicketDetail = TicketResponse.GetTicketDetail
        .from(orderRepository.findById(orderId).orElseThrow{NotFoundException(ErrorCode.ORDER_NOT_FOUND)})
}