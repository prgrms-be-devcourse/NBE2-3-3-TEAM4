package com.nbe2_3_3_team4.backend.domain.ticket.entity

import com.nbe2_3_3_team4.backend.domain.parking.entity.Parking
import com.nbe2_3_3_team4.backend.global.BaseTime
import jakarta.persistence.*

@Entity
@Table(name = "tickets")
class Ticket(parking: Parking?, price: Int?, parkingDuration: Int?) : BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val id: Long? = null

    @Column(name = "price")
    private val price : Int? =null

    @Column(name = "parking_duration")
    private val parkingDuration : Int? =null

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_id")
    val parking: Parking? = null

    companion object {
        fun to(parking: Parking?, price: Int, parkingDuration: Int): Ticket {
            return Ticket(
                    parking= parking,
                    price=price,
                    parkingDuration=parkingDuration)
        }
    }
}
