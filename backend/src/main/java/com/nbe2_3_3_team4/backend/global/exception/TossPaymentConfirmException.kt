package com.nbe2_3_3_team4.backend.global.exception

class TossPaymentConfirmException(val statusCode: Int, val msg: String) : RuntimeException() {
}