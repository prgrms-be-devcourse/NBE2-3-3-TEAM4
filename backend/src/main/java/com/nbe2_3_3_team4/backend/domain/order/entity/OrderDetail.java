package com.nbe2_3_3_team4.backend.domain.order.entity;

import com.nbe2_3_3_team4.backend.global.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "order_details")
public class OrderDetail extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime startParkingTime;
    private LocalDateTime endParkingTime;
    private int cancelPrice;
    private int addPrice;
    private int totalPrice;


}
