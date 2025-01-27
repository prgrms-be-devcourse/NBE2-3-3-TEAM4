package com.nbe2_3_3_team4.backend.domain.order.entity.enums

enum class OrderStatus(val message: String) {
    WAITING("주차 대기"),
    PARKING("주차 중"),
    FINISHED("주차 종료"),
    CANCELED("취소")
}