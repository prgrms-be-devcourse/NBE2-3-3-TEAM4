package com.nbe2_3_3_team4.backend.domain.parking.dto

import com.nbe2_3_3_team4.backend.domain.parking.entity.Parking
import com.nbe2_3_3_team4.backend.domain.parking.entity.ParkingStatus

class ParkingResponse {
    data class GetNearbyParking(val id: Long?, val name: String?, val latitude: Double?, val longitude: Double?, val status: String?) {
        companion object {
            @JvmStatic
            fun from(parking: Parking, status: String): GetNearbyParking {
                return GetNearbyParking(parking.parkingId, parking.name, parking.latitude, parking.longitude, status)
            }
        }
    }

    data class GetParking(val name: String?, val address: String?, val weOpenTime: String?, val weEndTime: String?,
                     val wdOpenTime: String?, val wdEndTime: String?, val basicCharge: Int?, val addCharge: Int?, val addChargeTime: Int?) {
        companion object {
            @JvmStatic
            fun from(parking: Parking): GetParking {
                return GetParking(parking.name, parking.address,
                        parking.weOpenTime, parking.weEndTime, parking.weOpenTime, parking.weEndTime,
                        parking.basicCharge, parking.addCharge, parking.addChargeTime)
            }
        }
    }

    data class GetParkingStatus(val availableSpace: Int?,
                           val usedSpace: Int?,
                           val totalSpace: Int?) {
        companion object {
            @JvmStatic
            fun from(status: ParkingStatus): GetParkingStatus {
                return GetParkingStatus(
                        status.totalParkingSpace - status.usedParkingSpace, status.usedParkingSpace, status.totalParkingSpace)
            }
        }
    }
}
