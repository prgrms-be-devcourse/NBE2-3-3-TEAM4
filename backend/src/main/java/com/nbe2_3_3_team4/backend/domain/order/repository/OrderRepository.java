package com.nbe2_3_3_team4.backend.domain.order.repository;

import com.nbe2_3_3_team4.backend.domain.order.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, String> {
}
