package com.nbe2_3_3_team4.backend.domain.tosspayment.dto

class TossPaymentRequest {
    data class TempAmountSession(
        val orderId: String,
        val amount: String
    )

    data class PaymentConfirmation(
        val orderId: String,
        val paymentKey: String,
        val amount: String
    )

    data class PaymentCancel(
        val orderId: String,
        val paymentKey: String,
        val cancelAmount: Long? = null,
        val cancelReason: String? = "사용자 요청으로 인한 결제 취소"
    )
}