package com.nbe2_3_3_team4.backend.global.exception

class TossPaymentException(val errorCode: ErrorCode) : RuntimeException() {
}