package com.nbe2_3_3_team4.backend.global.exception


class BadRequestException(val errorCode: ErrorCode) : RuntimeException() {
}

