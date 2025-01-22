package com.nbe2_3_3_team4.backend.domain.order.repository;

import com.nbe2_3_3_team4.backend.domain.order.entity.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {
}
