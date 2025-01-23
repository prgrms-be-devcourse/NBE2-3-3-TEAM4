package com.nbe2_3_3_team4.backend.global.exception;

import lombok.Getter;

/**
 * 오류 HttpStatus 코드 메세지 정의 enum 클래스
 */
@Getter
public enum ErrorCode { // 예외 발생시, body에 실어 날려줄 상태, code, message 커스텀

	//-1000: USER
	USER_ALREADY_EXIST(400, -1001, "이미 존재하는 회원입니다."),
	WRONG_SIGNUP(400, -1002, "올바르지 않은 회원가입입니다."),
	INVALID_PASSWORD(400, -1003, "비빌번호가 올바르지 않습니다."),
	USER_NOT_FOUND(404, -1004, "존재하지 않는 사용자입니다."),
	ADMIN_ALREADY_EXIST(400, -1005, "이미 존재하는 관리자입니다."),

	//-2000: JWT
	EMPTY_JWT_TOKEN(400, -2000, "JWT 토큰이 없습니다."),
	INVALID_ACCESS_TOKEN(400, -2001, "유효하지 않은 토큰입니다."),
	EXPIRED_ACCESS_TOKEN(400, -2002, "어세스 토큰이 만료되었습니다."),
	EXPIRED_REFRESH_TOKEN(400, -2003, "리프레시 토큰이 만료되었습니다."),
	INVALID_REFRESH_TOKEN(400, -2004, "잘못된 리프레시 토큰입니다."),
	INVALID_AUTHORITY_TOKEN(400, -2005, "권한 정보가 없는 토큰입니다."),

	//-3000: COMMON
	NOT_FOUND(404, -3000, "잘못된 경로입니다."),
	BAD_REQUEST(400, -3001, "유효하지 않은 요청입니다."),
	INVALID_REQUEST_BODY(400, -3002, "전달된 데이터가 올바르지 않습니다."),
	METHOD_NOT_ALLOWED(405, -3003, "잘못된 Http Method 입니다."),
	INTERNAL_SERVER_ERROR(500, -3004, "서버 내부 오류입니다."),
	UNAUTHORIZED(401, -3005, "토큰 정보가 만료되었거나 존재하지 않습니다."),
	FORBIDDEN(403, -3006, "접근 권한이 없습니다."),
	INVALID_SORT_TYPE(400, -3007, "올바르지 않은 정렬 타입입니다."),


	//-4000: PKLT(PARKING)
	PKLT_NOT_FOUND(404, -4000, "해당 주차장을 찾을 수 없습니다."),
	PKLT_FULL(400, -4001, "해당 주차장이 가득 찼습니다."),
	PKLT_ALREADY_PARKED(400, -4002, "이미 주차 중인 차량입니다."),

	//-5000: TICKET
	TICKET_NOT_FOUND(404, -5000, "주차권을 찾을 수 없습니다."),
	TICKET_ALREADY_EXIST(400, -5001, "해당 주차권이 이미 존재합니다."),

	//-6000: Order
	ORDER_NOT_FOUND(404, -6000, "주문을 찾을 수 없습니다"),
	ALREADY_ORDERED(400, -6001, "주차권이 이미 주문되었습니다."),
	NOT_PARKED(400, -6002, "아직 입차하지 않은 차량입니다."),
	PARKING_IN_PROGRESS(400, -6003, "아직 주차중입니다."),
	ORDER_CANCEL_UNAVAILABLE(400, -6004, "주차권을 취소할 수 없습니다."),

	//-7000 Car
	CAR_ALREADY_EXISTS(409, -7000, "이미 등록된 차량입니다."),
	CAR_NOT_PRIMARY(400, -7001, "해당 차량은 대표 차량이 아닙니다"),
	CAR_NOT_FOUND(404, -7002, "차량 정보를 찾을 수 없습니다.");


	// 1. status = 날려줄 상태코드
	// 2. code = 해당 오류가 어느부분과 관련있는지 카테고리화 해주는 코드. 예외 원인 식별하기 편하기에 추가
	// 3. message = 발생한 예외에 대한 설명.
	private final int status;
	private final int code;
	private final String message;

	ErrorCode(int status, int code, String message) {
		this.status = status;
		this.code = code;
		this.message = message;
	}
}
