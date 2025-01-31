package com.nbe2_3_3_team4.backend.controller


import com.nbe2_3_3_team4.backend.domain.order.dto.OrderRequest
import com.nbe2_3_3_team4.backend.domain.order.dto.OrderResponse
import com.nbe2_3_3_team4.backend.domain.order.service.OrderService
import com.nbe2_3_3_team4.backend.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*


@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order API", description = "주차권 주문 관련 API")
class OrderController(
    private val orderService: OrderService
) {

    @Operation(summary = "주차권 주문 요청 API", description = "주차권 구매를 요청합니다.")
    @ApiResponses(io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공"))
    @PostMapping("/tickets")
    fun createOrder(
        @RequestBody dto: OrderRequest.CreateOrder,
        @AuthenticationPrincipal user: User
    ): ResponseEntity<ApiResponse<OrderResponse.CreateOrder>> {
        val response = orderService.createOrder(dto, user.username)
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.createSuccess(response))
    }

    @Operation(summary = "주차권 주문 결제 성공 API", description = "주차권 결제 성공 시 상태 변경합니다")
    @ApiResponses(io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공"))
    @PutMapping("/{payId}/success")
    fun completePay(@PathVariable payId: String): ResponseEntity<ApiResponse<String>> {
        val response = orderService.completePay(payId)
        return ResponseEntity.ok(ApiResponse.createSuccess(response))
    }

    @Operation(summary = "주차권 구매 기록 상세 조회 API", description = "주차권 구매 세부 기록을 조회합니다.")
    @ApiResponses(io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공"))
    @GetMapping("/{orderId}")
    fun getOrder(@PathVariable orderId: String): ResponseEntity<ApiResponse<OrderResponse>> {
        val response = orderService.getOrder(orderId)
        return ResponseEntity.ok(ApiResponse.createSuccess(response))
    }

    @Operation(summary = "주차권 취소 요청 API", description = "주차권 취소를 요청합니다.")
    @ApiResponses(io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공"))
    @PutMapping("/{orderId}/cancel")
    fun cancelTicket(
        @AuthenticationPrincipal user: User,
        @PathVariable orderId: String
    ): ResponseEntity<ApiResponse<String>> {
        val response = orderService.cancelTicket(user.username, orderId)
        return ResponseEntity.ok(ApiResponse.createSuccess(response))
    }

    @DeleteMapping("/{payId}/pay")
    fun deleteOrder(@PathVariable payId: String): ResponseEntity<ApiResponse<Void?>> {
        return ResponseEntity.ok(ApiResponse.createSuccess(orderService.deleteOrder(payId)))
    }

    @DeleteMapping("/{orderId}")
    fun deleteOrderById(@PathVariable orderId: String): ResponseEntity<ApiResponse<Void?>> {
        return ResponseEntity.ok(ApiResponse.createSuccess(orderService.deleteOrderById(orderId)))
    }

    @GetMapping("/{orderId}/payments")
    fun getOrderForPayments(@PathVariable orderId: String): ResponseEntity<ApiResponse<OrderResponse.getOrderForPayment>> {
        return ResponseEntity.ok(ApiResponse.createSuccess(orderService.getOrderForPayment(orderId)))
    }

    @Operation(summary = "주차권 구매 기록 조회 API", description = "주차권 구매 기록을 조회합니다.")
    @ApiResponses(io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공"))
    @GetMapping("/order-history")
    fun getOrderHistory(@AuthenticationPrincipal user: User): ResponseEntity<ApiResponse<List<OrderResponse.GetOrderHistory>>> {
        val response = orderService.getOrderHistory(user.username)
        return ResponseEntity.ok(ApiResponse.createSuccess(response))
    }
}