package com.nbe2_3_3_team4.backend.domain.order.service;

import com.nbe2_3_3_team4.backend.domain.order.repository.OrderDetailRepository;
import com.nbe2_3_3_team4.backend.domain.order.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;

    private String getRandomOrderId() {
        return UUID.randomUUID().toString();
    }
}
