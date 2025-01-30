package com.nbe2_3_3_team4.backend.domain.ticket.dto

import com.nbe2_3_3_team4.backend.domain.ticket.entity.Ticket

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
}