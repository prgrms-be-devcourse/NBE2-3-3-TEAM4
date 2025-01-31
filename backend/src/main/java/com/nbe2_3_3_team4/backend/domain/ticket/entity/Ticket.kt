package com.nbe2_3_3_team4.backend.domain.ticket.entity

import com.nbe2_3_3_team4.backend.domain.parking.entity.Parking
import com.nbe2_3_3_team4.backend.global.BaseTime
import jakarta.persistence.*
@Entity
@Table(name = "tickets")
class Ticket(
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parking_id")
    var parking: Parking? = null,

    @Column(name = "price")
    var price: Int? = null,

    @Column(name = "parking_duration")
    var parkingDuration: Int? = null
) : BaseTime() {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null

    companion object {
        @JvmStatic
        fun to(parking: Parking?, price: Int, parkingDuration: Int): Ticket {
            return Ticket(parking = parking, price = price, parkingDuration = parkingDuration)
        }
    }
}
