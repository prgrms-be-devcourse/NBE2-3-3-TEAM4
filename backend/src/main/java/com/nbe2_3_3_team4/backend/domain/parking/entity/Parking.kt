package com.nbe2_3_3_team4.backend.domain.parking.entity

import com.fasterxml.jackson.databind.JsonNode
import com.nbe2_3_3_team4.backend.domain.ticket.entity.Ticket
import com.nbe2_3_3_team4.backend.global.BaseTime
import jakarta.persistence.*

@Entity
@Table(name = "parking")
class Parking() : BaseTime() {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "parking_id")
    var parkingId: Long? = null

    @Column(name = "name")
    var name: String? = null

    @Column(name = "address")
    var address: String? = null

    @Column(name = "latitude")
    var latitude: Double? = null

    @Column(name = "longitude")
    var longitude: Double? = null

    @Column(name = "we_open_time", length = 4)
    var weOpenTime: String? = null

    @Column(name = "we_end_time", length = 4)
    var weEndTime: String? = null

    @Column(name = "basic_charge")
    var basicCharge : Int? =null

    @Column(name = "basic_charge_time")
    var basicChargeTime : Int? =null

    @Column(name = "add_charge")
    var addCharge : Int? =null

    @Column(name = "add_charge_time")
    var addChargeTime : Int? =null

    @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    @JoinColumn(name = "pklt_status_id")
    var parkingStatus: ParkingStatus? = null

    @OneToMany(mappedBy = "parking", cascade = [CascadeType.ALL], orphanRemoval = true)
    val tickets: MutableList<Ticket> = ArrayList()

    constructor(name: String?, address: String?, latitude: Double, longitude: Double, weOpenTime: String?, weEndTime: String?, wdOpenTime: String?, wdEndTime: String?, basicCharge: Int, basicChargeTime: Int, addCharge: Int, addChargeTime: Int, parkingStatus: ParkingStatus?) : this() {
        this.name = name
        this.address = address
        this.latitude = latitude
        this.longitude = longitude
        this.weOpenTime = weOpenTime
        this.weEndTime = weEndTime
        this.basicCharge = basicCharge
        this.basicChargeTime = basicChargeTime
        this.addCharge = addCharge
        this.addChargeTime = addChargeTime
        this.parkingStatus = parkingStatus
    }

    fun regTicket(ticket: Ticket) {
        tickets.add(ticket)
    }

    companion object {
        @JvmStatic
		fun to(data: JsonNode, status: ParkingStatus?): Parking {
            var addCharge = data["add_prk_crg"].asInt()
            var addChargeTime = data["add_prk_hr"].asInt()

            if (data["add_prk_crg"].asInt() == 0) {
                addCharge = 220
                addChargeTime = 5
            }

            return Parking(
                    name = data["pklt_nm"].asText(),
                    address=data["addr"].asText()
                    ,latitude=data["lat"].asDouble()
                    ,longitude=data["lot"].asDouble()
                    ,weOpenTime=data["we_oper_bgng_tm"].asText()
                    ,weEndTime=data["we_oper_end_tm"].asText()
                    ,wdOpenTime=data["wd_oper_bgng_tm"].asText()
                    ,wdEndTime=data["wd_oper_end_tm"].asText()
                    ,basicCharge=data["bsc_prk_crg"].asInt()
                    ,basicChargeTime=data["bsc_prk_hr"].asInt()
                    ,addCharge=addCharge
                    ,addChargeTime=addChargeTime
                    ,parkingStatus=status)
        }
    }

}
