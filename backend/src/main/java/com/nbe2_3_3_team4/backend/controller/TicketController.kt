package com.nbe2_3_3_team4.backend.controller

import com.nbe2_3_3_team4.backend.domain.ticket.dto.TicketResponse
import com.nbe2_3_3_team4.backend.domain.ticket.service.TicketService
import com.nbe2_3_3_team4.backend.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation

import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "🎫 Ticket", description = "주차권 관련 API")
class TicketController(val ticketService: TicketService) {

    @Operation(summary = "주차권 조회 API", description = "주차권 조회합니다")
    @ApiResponses(io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    @GetMapping("/{ticketId}")
    fun getTicketById(@PathVariable(value = "ticketId") ticketId: Long): ResponseEntity<ApiResponse<TicketResponse.GetTicket>> {
        return ResponseEntity.ok()
            .body(ApiResponse.createSuccess(ticketService.getTicket(ticketId)))
    }
}