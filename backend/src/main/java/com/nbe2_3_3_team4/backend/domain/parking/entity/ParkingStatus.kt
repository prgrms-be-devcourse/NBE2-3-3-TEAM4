package com.nbe2_3_3_team4.backend.domain.parking.entity

import com.fasterxml.jackson.databind.JsonNode
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "parking_status")
class ParkingStatus (
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var parkingStatusId: Long? = null,
    var totalParkingSpace: Int = 0, // 총 주차 면
    var usedParkingSpace: Int = 0, // 사용중인 주차 면
    var space_updated_at: LocalDateTime? = null // 사용 주차면 업데이트 시간
) {
    fun modifyTotalParkingSpaceOfJson() { this.totalParkingSpace += 1 }

    fun updateUsedParkingSpace() {
        this.usedParkingSpace +=1
    }

    fun decreaseUsedParkingSpace() {
        this.usedParkingSpace -= 1
    }


    companion object {
		fun to(data: JsonNode): ParkingStatus {
            return ParkingStatus ( null, data["tpkct"].asInt(), data["now_prk_vhcl_cnt"].asInt(), LocalDateTime.now() )
        }
    }
}
