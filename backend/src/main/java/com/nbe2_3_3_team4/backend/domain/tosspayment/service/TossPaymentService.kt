package com.nbe2_3_3_team4.backend.domain.tosspayment.service


import com.nbe2_3_3_team4.backend.domain.order.entity.Order
import com.nbe2_3_3_team4.backend.domain.order.entity.enums.PaymentStatus
import com.nbe2_3_3_team4.backend.domain.order.repository.OrderRepository
import com.nbe2_3_3_team4.backend.domain.parking.entity.ParkingStatus
import com.nbe2_3_3_team4.backend.domain.parking.repository.ParkingStatusRepository
import com.nbe2_3_3_team4.backend.domain.tosspayment.dto.TossPaymentRequest.PaymentConfirmation
import com.nbe2_3_3_team4.backend.domain.tosspayment.dto.TossPaymentRequest.TempAmountSession
import com.nbe2_3_3_team4.backend.global.exception.*
import jakarta.servlet.http.HttpSession
import org.json.JSONObject
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.IOException
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

@Service
class TossPaymentService(
    val orderRepository: OrderRepository,
    val parkingStatusRepository: ParkingStatusRepository,

) {

    private val logger: Logger = LoggerFactory.getLogger(TossPaymentService::class.java)
    private val secretKey: String = "test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6"
    /**
     * 결제 전 금액 세션에 저장 메서드
     *
     * @param session
     * @param requestDto
     */
    fun createTempPaymentAmount(
        session: HttpSession,
        requestDto: TempAmountSession
    ) {
        session.setAttribute(requestDto.orderId, requestDto.amount)
    }

    /**
     * 결제 인증 후 금액과 결제 전 금액 비교하여 검증, 세션 데이터 삭제 메서드
     *
     * @param session
     * @param requestDto
     */
    fun verifyPaymentAmountAndRemoveSession(session: HttpSession, requestDto: TempAmountSession) {
        val amount = session.getAttribute(requestDto.orderId) as String
        verifyPaymentAmount(amount, requestDto.amount)
        // 검증에 사용했던 세션은 삭제
        session.removeAttribute(requestDto.orderId)
    }

    private fun verifyPaymentAmount(expectedAmount: String, actualAmount: String) {
        if (expectedAmount != actualAmount) {
            throw BadRequestException(ErrorCode.INVALID_PAYMENT_AMOUNT)
        }
    }

    /**
     * 결제 인증 후 토스페이먼츠 결제 승인 요청
     *
     * @param requestDto TosspaymentRequest.PaymentConfirmation
     * @return Long
     */
    fun confirmPayment(requestDto: PaymentConfirmation): String {
        var order: Order? = null
        var confirmPaymentResponse: JSONObject? = null

        // 미리 생성한 결제대기 상태 주문 조회
        order = orderRepository.findById(requestDto.orderId)
            .orElse(null) ?: throw NotFoundException(ErrorCode.ORDER_NOT_FOUND)

        //토스에 결제요청
        confirmPaymentResponse = requestPaymentConfirmation(requestDto, order)

        // 결제 성공 처리
        updateOrderPaymentAndStatus(order, confirmPaymentResponse)
        return order.id
    }

    // 토스페이먼츠 결제 승인 API 요청
    private fun requestPaymentConfirmation(requestDto: PaymentConfirmation, order: Order?): JSONObject {
        try {
            // 헤더 인코딩
            val authorizationHeader = authorizationHeader
            // API 요청
            val response = requestConnect(requestDto, authorizationHeader)

            val responseCode = response.statusCode()
            logger.info(responseCode.toString() + " " + response.body())

            val responseJson = JSONObject(response.body())

            // 결제 승인 요청 실패
            if (responseCode != 200) {
                logger.error("결제 승인 요청 실패")
                if (Objects.nonNull(order)) {
                    // 주문 삭제
                    deleteOrder(order!!)
                    // 자리 복구
                    updateParkingStatusCnt(order.ticket.parking?.parkingStatus)
                }
                throw TossPaymentConfirmException(responseCode, (responseJson["message"] as String))
            }
            logger.info(responseJson["orderName"].toString() + responseJson["totalAmount"].toString())
            return responseJson
        } catch (e: IOException) {
            logger.error("결제 승인 요청 값 오류")
            if (Objects.nonNull(order)) {
                deleteOrder(order!!)
                updateParkingStatusCnt(order.ticket.parking?.parkingStatus)
            }
            throw TossPaymentException(ErrorCode.TOSS_PAYMENT_CONFIRM_REQUEST_ERROR)
        } catch (e: InterruptedException) {
            logger.error("파싱 실패")
            if (Objects.nonNull(order)) {
                deleteOrder(order!!)
                updateParkingStatusCnt(order.ticket.parking?.parkingStatus)
            }
            throw TossPaymentException(ErrorCode.INVALID_PAYMENT_RESPONSE_JSON)
        }
    }

    private fun handlePaymentCancellation(jsonObject: JSONObject) {
        try {
            val paymentKey = jsonObject["paymentKey"] as String
            requestPaymentCancel(paymentKey, CANCEL_REASON)
            throw BadRequestException(ErrorCode.INTERNAL_SERVER_ERROR)
        } catch (e: Exception) {
            throw TossPaymentException(ErrorCode.TOSS_PAYMENT_CANCEL_REQUEST_ERROR)
        }
    }

    @Throws(IOException::class, InterruptedException::class)
    private fun requestConnect(requestDto: PaymentConfirmation, authorizationHeader: String): HttpResponse<String> {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.tosspayments.com/v1/payments/confirm"))
            .header("Authorization", authorizationHeader)
            .header("Content-Type", "application/json")
            .method(
                "POST", HttpRequest.BodyPublishers.ofString(
                    ("{\"paymentKey\":\"" + requestDto.paymentKey
                            + "\",\"orderId\":\"" + requestDto.orderId
                            + "\",\"amount\":\"" + requestDto.amount
                            + "\"}")
                )
            )
            .build()
        return HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString())
    }

    @Throws(IOException::class, InterruptedException::class)
    private fun requestPaymentCancel(paymentKey: String, cancelReason: String) {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.tosspayments.com/v1/payments/$paymentKey/cancel"))
            .header("Authorization", authorizationHeader)
            .header("Content-Type", "application/json")
            .method(
                "POST",
                HttpRequest.BodyPublishers.ofString("{\"cancelReason\":\"$cancelReason\"}")
            )
            .build()

        val response = HttpClient.newHttpClient()
            .send(request, HttpResponse.BodyHandlers.ofString())

        logger.info("결제 취소 paymentKey: {}, response: {}", paymentKey, response.body())
    }

    private val authorizationHeader: String
        // 토스페이먼츠와 연동하기 위한 인증 Header 설정
        get() {
            // "Basic" + 토스페이먼츠 API 시크릿 키 + ":" -> base64인코딩
            val encoder = Base64.getEncoder()
            val encodedBytes = encoder.encode(("$secretKey:").toByteArray(StandardCharsets.UTF_8))
            return "Basic " + String(encodedBytes)
        }

    private fun updateOrderPaymentAndStatus(order: Order, jsonObject: JSONObject) {
        try {
            val paymentKey = jsonObject.getString("paymentKey")
            val paymentDate = parseLocalDateTime(jsonObject["approvedAt"] as String)

            order.updatePaymentInfo(paymentKey, paymentDate)
            order.updatePaymentStatus(PaymentStatus.COMPLETE)
            orderRepository.save(order)
        } catch (e: Exception) {
            logger.error("결제 성공 후 비즈니스 로직 오류")
            handlePaymentCancellation(jsonObject)
            deleteOrder(order)
            updateParkingStatusCnt(order.ticket.parking?.parkingStatus)
            throw BadRequestException(ErrorCode.INTERNAL_SERVER_ERROR)
        }
    }

    private fun parseLocalDateTime(dateTimeString: String): LocalDateTime {
        return LocalDateTime.parse(dateTimeString, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    }

    private fun deleteOrder(order: Order) {
        orderRepository.delete(order)
    }

    private fun updateParkingStatusCnt(parkingStatus: ParkingStatus?) {
        parkingStatus?.decreaseUsedParkingSpace()
        parkingStatusRepository.save(parkingStatus!!)
    }

    companion object {
        private const val CANCEL_REASON = "결제 성공 처리 중 오류 발생"
    }
}