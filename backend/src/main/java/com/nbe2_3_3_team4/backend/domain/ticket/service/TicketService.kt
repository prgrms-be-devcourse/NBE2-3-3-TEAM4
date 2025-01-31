package com.nbe2_3_3_team4.backend.domain.ticket.service

import com.nbe2_3_3_team4.backend.domain.ticket.dto.TicketResponse
import com.nbe2_3_3_team4.backend.domain.ticket.repository.TicketRepository
import com.nbe2_3_3_team4.backend.global.exception.ErrorCode
import com.nbe2_3_3_team4.backend.global.exception.NotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TicketService(
    val ticketRepository: TicketRepository
) {

    @Transactional(readOnly = true)
    fun getTicket(id: Long): TicketResponse.GetTicket {
        val ticket = ticketRepository.findById(id)
            .orElseThrow{NotFoundException(ErrorCode.TICKET_NOT_FOUND)}

        return TicketResponse.GetTicket.from(ticket)
    }
}