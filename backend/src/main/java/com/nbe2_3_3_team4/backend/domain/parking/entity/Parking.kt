package com.nbe2_3_3_team4.backend.domain.parking.entity

import com.fasterxml.jackson.databind.JsonNode
import com.nbe2_3_3_team4.backend.domain.ticket.entity.Ticket
import jakarta.persistence.*

@Entity
@Table(name = "parking")
class Parking(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parking_id")
    var parkingId: Long? = null,
    @Column(name = "name")
    var name: String? = null,
    @Column(name = "address")
    var address: String? = null,
    @Column(name = "latitude")
    var latitude: Double? = null,
    @Column(name = "longitude")
    var longitude: Double? = null,
    @Column(name = "we_open_time", length = 4)
    var weOpenTime: String? = null,
    @Column(name = "we_end_time", length = 4)
    var weEndTime: String? = null,
    @Column(name = "wd_open_time", length = 4)
    var wdOpenTime: String? = null,
    @Column(name = "wd_end_time", length = 4)
    var wdEndTime: String? = null,
    @Column(name = "basic_charge")
    var basicCharge: Int = 0,
    @Column(name = "basic_charge_time")
    var basicChargeTime: Int = 0,
    @Column(name = "add_charge")
    var addCharge: Int = 0,
    @Column(name = "add_charge_time")
    var addChargeTime: Int = 0,

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "pklt_status_id")
    var parkingStatus: ParkingStatus? = null,

    @OneToMany(mappedBy = "parking", cascade = [CascadeType.ALL], orphanRemoval = true)
    var tickets: MutableList<Ticket> = mutableListOf()
) {
    fun regTicket(ticket: Ticket) { tickets.add(ticket) }

    companion object {
        @JvmStatic
        fun to(data: JsonNode, status: ParkingStatus?): Parking {
            val addCharge = if (data["add_prk_crg"].asInt() == 0) 220 else data["add_prk_crg"].asInt()
            val addChargeTime = if (data["add_prk_crg"].asInt() == 0) 5 else data["add_prk_hr"].asInt()

            return Parking(
                name = data["pklt_nm"].asText(),
                address = data["addr"].asText(),
                latitude = data["lat"].asDouble(),
                longitude = data["lot"].asDouble(),
                weOpenTime = data["we_oper_bgng_tm"].asText(),
                weEndTime = data["we_oper_end_tm"].asText(),
                wdOpenTime = data["wd_oper_bgng_tm"].asText(),
                wdEndTime = data["wd_oper_end_tm"].asText(),
                basicCharge = data["bsc_prk_crg"].asInt(),
                basicChargeTime = data["bsc_prk_hr"].asInt(),
                addCharge = addCharge,
                addChargeTime = addChargeTime,
                parkingStatus = status,
            )
        }
    }
}
