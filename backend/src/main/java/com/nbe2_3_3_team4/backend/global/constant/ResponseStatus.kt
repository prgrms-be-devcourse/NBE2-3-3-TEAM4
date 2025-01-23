package com.nbe2_3_3_team4.backend.global.constant;

import lombok.Getter;

@Getter
public enum ResponseStatus {
    SUCCESS("성공"),
    ERROR("오류");

    private final String msg;

    ResponseStatus(String msg) {
        this.msg= msg;
    }
}
