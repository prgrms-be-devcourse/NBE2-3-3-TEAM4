package com.nbe2_3_3_team4.backend.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class DuplicateException extends RuntimeException {
	private final ErrorCode errorCode;
}
