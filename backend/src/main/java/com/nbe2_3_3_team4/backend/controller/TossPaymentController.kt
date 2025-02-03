package com.nbe2_3_3_team4.backend.controller

import com.nbe2_3_3_team4.backend.domain.tosspayment.dto.TossPaymentRequest.PaymentConfirmation
import com.nbe2_3_3_team4.backend.domain.tosspayment.dto.TossPaymentRequest.TempAmountSession
import com.nbe2_3_3_team4.backend.domain.tosspayment.service.TossPaymentService
import com.nbe2_3_3_team4.backend.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpSession
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration


@RestController
@RequestMapping("/api/toss-payments")
@Tag(name = "💲TossPayment", description = "토스 결제 관련 API")
class TossPaymentController(val tossPaymentService: TossPaymentService) {

    /**
     * 결제를 요청하기 전에 orderNumber와 amount를 세션에 저장하는 controller (결제 요청과 승인 사이에 데이터 무결성을 확인)
     * @param session HttpSession
     * @param requestDto TosspaymentRequest.TempAmountSession
     * @return null
     */
    @Operation(summary = "결제 전 주문금액 세션에 임시 저장 API", description = "주문금액을 세션에 저장합니다.")
    @ApiResponses(
        value = [io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "임시 저장 성공"
        )]
    )
    @PostMapping("/amounts/session")
    fun createTempPaymentAmount(
        session: HttpSession,
        @RequestBody requestDto: TempAmountSession
    ): ResponseEntity<ApiResponse<Any>> {
        val sessionId: String = tossPaymentService.createTempPaymentAmount(
            session,
            requestDto
        )

        val cookie: ResponseCookie = ResponseCookie
            .from("JSESSIONID", sessionId)
            .domain("localhost")
            .path("/")
            .httpOnly(true)
            .secure(false) // 로컬이므로 제외
            .maxAge(Duration.ofDays(30))
            .sameSite("Strict")
            .build()

        return ResponseEntity.status(HttpStatus.CREATED)
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(ApiResponse.createSuccessWithNoData())
    }

    /**
     * 세션에 저장해둔 amount와 결제 후 amount 비교하여 검증 controller
     * @param session HttpSession
     * @param requestDto TosspaymentRequest.TempAmountSession
     * @return null
     */
    @Operation(summary = "세션에 저장된 금액과 실제 결제 금액 검증 API")
    @ApiResponses(
        value = [io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "검증 성공"
        )]
    )
    @PostMapping("/amounts/verify")
    fun verifyPaymentAmount(
        httpServletRequest: HttpServletRequest,
        @RequestBody requestDto: TempAmountSession
    ): ResponseEntity<ApiResponse<Any>> {
        val session: HttpSession = httpServletRequest.getSession(false)
        tossPaymentService.verifyPaymentAmountAndRemoveSession(session, requestDto)
        return ResponseEntity.ok().body(ApiResponse.createSuccessWithNoData())
    }


    @Operation(summary = "토스페이먼츠에 결제 승인 요청 API")
    @ApiResponses(
        value = [io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "승인 성공"
        )]
    )
    @PostMapping("/confirm")
    fun confirmPayment(@RequestBody requestDto: PaymentConfirmation): ResponseEntity<ApiResponse<String>> {
        val orderId = tossPaymentService.confirmPayment(requestDto)
        return ResponseEntity.ok().body(ApiResponse.createSuccess(orderId))
    }
}