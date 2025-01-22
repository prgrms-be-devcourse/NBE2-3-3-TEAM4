package com.nbe2_3_3_team4.backend.domain.order.entity.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {

    WAITING("결제 대기"), COMPLETE("결제 완료");

    private final String message;
}
