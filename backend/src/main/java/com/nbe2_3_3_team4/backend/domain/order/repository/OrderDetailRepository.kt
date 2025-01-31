package com.nbe2_3_3_team4.backend.domain.order.repository


import com.nbe2_3_3_team4.backend.domain.order.entity.OrderDetail
import org.springframework.data.jpa.repository.JpaRepository

interface OrderDetailRepository : JpaRepository<OrderDetail, Long> {
}