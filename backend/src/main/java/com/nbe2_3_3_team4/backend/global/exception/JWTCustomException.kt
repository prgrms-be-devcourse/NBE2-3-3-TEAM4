package com.nbe2_3_3_team4.backend.global.exception

class JWTCustomException(val errorCode: ErrorCode) : RuntimeException() {
}
