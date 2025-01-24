package com.nbe2_3_3_team4.backend.domain.tosspayment.dto

class TossPaymentRequest {
    class TempAmountSession(
        val orderId: String,
        val amount: String
    )

    class PaymentConfirmation(
        val orderId: String,
        val paymentKey: String,
        val amount: String
    )
}