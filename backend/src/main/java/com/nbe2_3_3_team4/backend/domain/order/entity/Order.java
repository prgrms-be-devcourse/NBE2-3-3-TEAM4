package com.nbe2_3_3_team4.backend.domain.order.entity;

import com.nbe2_3_3_team4.backend.domain.member.entity.Member;
import com.nbe2_3_3_team4.backend.domain.parking.entity.ParkingStatus;
import com.nbe2_3_3_team4.backend.domain.ticket.entity.Ticket;
import com.nbe2_3_3_team4.backend.global.BaseTime;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseTime {

    @Id
    @Column(name = "order_id")
    private String id;

    private String status;
    private String paymentDate;
    private String paymentKey;
    private String paymentStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    @JoinColumn(name = "order_detail_id")
    private OrderDetail orderDetail;
}
