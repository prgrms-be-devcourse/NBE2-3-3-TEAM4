package com.nbe2_3_3_team4.backend.global.exception

import com.nbe2_3_3_team4.backend.global.response.ApiResponse
import jakarta.persistence.EntityNotFoundException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.BindingResult
import org.springframework.validation.ObjectError
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.servlet.NoHandlerFoundException
import java.io.IOException
import java.util.stream.Collectors


/**
 * 공통 예외처리 클래스
 */
@RestControllerAdvice
class GlobalExceptionHandler {


    private val log = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    private inline fun <reified GlobalExceptionHandler> GlobalExceptionHandler.logger() = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)!!

    // 중복 체크
    @ExceptionHandler(DuplicateException::class)
    fun handleDuplicateAccountException(e: DuplicateException): ResponseEntity<ApiResponse<Any>> {
        logger().error("[DuplicateAccountException] message: {}", e.message)
        return ResponseEntity.status(e.errorCode.status)
                .body(ApiResponse.createError(e.errorCode.message))
    }


    // validation 체크
    private fun bindingResultErrorsCheck(bindingResult: BindingResult): String {
        val errorMap: MutableMap<String, String> = HashMap()
        for (fe in bindingResult.fieldErrors) {
            errorMap[fe.field] = fe.defaultMessage.toString()
        }
        return errorMap.toString()
    }

    // 잘못된 경로 에러 404
    @ExceptionHandler(NoHandlerFoundException::class)
    fun handleNoHandlerFoundException(e: NoHandlerFoundException): ResponseEntity<ApiResponse<Any>> {
        logger().error("[NoHandlerFoundException] message: {}", e.message)
        val errorCode = ErrorCode.NOT_FOUND
        return ResponseEntity.status(errorCode.status)
                .body(ApiResponse.createError(errorCode.message))
    }

    // Http Method 에러
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupportedException(e: HttpRequestMethodNotSupportedException): ResponseEntity<ApiResponse<Any>> {
        logger().error("[HttpRequestMethodNotSupportedException] message: {}", e.message)
        val errorCode = ErrorCode.METHOD_NOT_ALLOWED
        return ResponseEntity.status(errorCode.status)
                .body(ApiResponse.createError(errorCode.message))
    }

    // IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(e: IllegalArgumentException): ResponseEntity<ApiResponse<Any>> {
        logger().error("[IllegalArgumentException] message: {}", e.message)
        val errorCode = ErrorCode.BAD_REQUEST
        return ResponseEntity.status(errorCode.status)
                .body(ApiResponse.createErrorWithMsg(e.message))
    }

    // 각종 400 에러
    @ExceptionHandler(BadRequestException::class)
    fun handleBadRequestException(e: BadRequestException): ResponseEntity<ApiResponse<Any>> {
        logger().error("[BadRequestException] message: {}", e.message)
        return ResponseEntity.status(e.errorCode.status)
                .body(ApiResponse.createError(e.errorCode.message))
    }

    // 각종 404 에러
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFoundException(e: NotFoundException): ResponseEntity<ApiResponse<Any>> {
        logger().error("[NotFoundException] message: {}", e.message)
        return ResponseEntity.status(e.errorCode.status)
                .body<ApiResponse<Any>>(ApiResponse.createError(e.errorCode.message))
    }

    // 유효성 검사 에러
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
            e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Any>> {
        logger().error("[MethodArgumentNotValidException] message: {}", e.message)
        val errorMessage = e.bindingResult.allErrors.stream()
                .map { obj: ObjectError -> obj.defaultMessage }
                .collect(Collectors.joining("\n"))
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.createError(errorMessage))
    }

    // Entity 조회 실패 시 에러
    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(e: EntityNotFoundException): ResponseEntity<ApiResponse<Any>> {
        logger().error("[EntityNotFoundException] message: {}", e.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.createError(e.message))
    }

    @ExceptionHandler(IOException::class)
    fun handleIOException(e: IOException): ResponseEntity<ApiResponse<Any>> {
        logger().error("[IOException] message: {}", e.message)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ApiResponse.createError(e.message))
    }

    @ExceptionHandler(JWTCustomException::class)
    fun handleJwtExceptionException(e: JWTCustomException): ResponseEntity<ApiResponse<Any>> {
        logger().error("[JwtExceptionError] message: {}", e.errorCode.message)
        return ResponseEntity.status(e.errorCode.status)
                .body(ApiResponse.createError(e.errorCode.message))
    }

    // TossPayment 결제승인 에러
    @ExceptionHandler(TossPaymentConfirmException::class)
    fun handleTossPaymentException(e: TossPaymentConfirmException): ResponseEntity<ApiResponse<Any>> {
        log.error("[TossPaymentConfirmException] message: {}", e.msg)
        return ResponseEntity.status(e.statusCode)
            .body(ApiResponse.createError(e.msg))
    }

    // TossPayment 에러
    @ExceptionHandler(TossPaymentException::class)
    fun handleTossPaymentException(e: TossPaymentException): ResponseEntity<ApiResponse<Any>> {
        log.error("[TossPaymentException] message: {}", e.message)
        return ResponseEntity.status(e.errorCode.status)
                .body(ApiResponse.createError(e.errorCode.message))
    }

    // 위의 경우를 제외한 모든 에러 500
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): ResponseEntity<ApiResponse<Any>> {
        logger().error("[Exception] message: {},{}", e.message, e.javaClass, e.cause)
        val errorCode = ErrorCode.INTERNAL_SERVER_ERROR
        return ResponseEntity.status(errorCode.status)
                .body(ApiResponse.createError(errorCode.message))
    }
}
