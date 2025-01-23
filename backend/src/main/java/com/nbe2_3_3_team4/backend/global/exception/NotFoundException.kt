package com.nbe2_3_3_team4.backend.global.exception

import lombok.Getter

@Getter
class NotFoundException(val errorCode: ErrorCode) : RuntimeException() {
}
