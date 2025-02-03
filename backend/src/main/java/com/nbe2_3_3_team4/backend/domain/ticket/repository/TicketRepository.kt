package com.nbe2_3_3_team4.backend.domain.ticket.repository

import com.nbe2_3_3_team4.backend.domain.ticket.entity.Ticket
import org.springframework.data.jpa.repository.JpaRepository

interface TicketRepository : JpaRepository< Ticket, Long >{
}