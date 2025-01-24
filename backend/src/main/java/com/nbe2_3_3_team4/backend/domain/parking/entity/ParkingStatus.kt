package com.nbe2_3_3_team4.backend.domain.parking.entity


import com.fasterxml.jackson.databind.JsonNode
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "parking_status")
class ParkingStatus(var totalParkingSpace: Int, var usedParkingSpace: Int, private var space_updated_at: LocalDateTime) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private val parkingStatusId: Long? = null


    fun modifyTotalParkingSpaceOfJson(){
        this.totalParkingSpace += 1;
    }

    fun decreaseUsedParkingSpace() {
        this.usedParkingSpace -= 1
    }

    companion object {
        @JvmStatic
        fun to(data: JsonNode): ParkingStatus {
            return ParkingStatus(
                    totalParkingSpace = data["tpkct"].asInt(),
                    usedParkingSpace= data["now_prk_vhcl_cnt"].asInt(),
                    space_updated_at = LocalDateTime.now())
        }
    }
}
