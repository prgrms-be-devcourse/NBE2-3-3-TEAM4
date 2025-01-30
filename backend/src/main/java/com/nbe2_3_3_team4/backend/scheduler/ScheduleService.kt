package com.nbe2_3_3_team4.backend.scheduler

import com.nbe2_3_3_team4.backend.domain.order.entity.enum.OrderStatus.*
import com.nbe2_3_3_team4.backend.domain.order.repository.OrderRepository
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
class ScheduleService (
    private val orderRepository: OrderRepository
){
    // 1분마다 실행
    @Scheduled(fixedRate = 1000 * 60) // ms 단위
    @Transactional
    fun updateExpiredOrders() {
        val timeThreshold = LocalDateTime.now().minusMinutes(70)  // 현재 시간에서 1시간 10분을 뺀 시간

        // 'created_at'이 1시간 10분 이상 경과한 주문을 찾고 갱신
        orderRepository.findAllByCreatedAtBeforeAndOrderStatus(timeThreshold, WAITING).forEach { order ->
            order.updateOrderStatus(CANCELED) // 주문 상태를 '만료'로 변경 / 추수 EXPIRED 로 변경 할지 고민
            order.ticket.price?.let {
                order.orderDetail.updateCancelPrice(it)
                order.orderDetail.updateTotalPrice(it)
            } // 취소 수수료 계산
        }
    }

}