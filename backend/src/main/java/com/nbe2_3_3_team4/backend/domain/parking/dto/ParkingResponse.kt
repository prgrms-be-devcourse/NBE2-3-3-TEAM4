package com.nbe2_3_3_team4.backend.domain.parking.dto

import com.nbe2_3_3_team4.backend.domain.parking.entity.Parking
import com.nbe2_3_3_team4.backend.domain.parking.entity.ParkingStatus
import com.nbe2_3_3_team4.backend.domain.ticket.entity.Ticket

class ParkingResponse {
    data class GetNearbyParking(
        val id: Long?, val name: String?, val latitude: Double?, val longitude: Double?, val status: String?
    ) {
        companion object {
            fun from(parking: Parking, status: String) = with(parking) {
                GetNearbyParking(parkingId, name, latitude, longitude, status)
            }
        }
    }

    data class GetParking(
        val name: String?,
        val address: String?,
        val weOpenTime: String?,
        val weEndTime: String?,
        val wdOpenTime: String?,
        val wdEndTime: String?,
        val basicCharge: Int,
        val addCharge: Int,
        val addChargeTime: Int,
        val latitude: Double?,
        val longitude: Double?
    ) {
        companion object {
            fun from(parking: Parking) = with(parking) {
                GetParking(
                    name,
                    address,
                    weOpenTime,
                    weEndTime,
                    wdOpenTime,
                    wdEndTime,
                    basicCharge,
                    addCharge,
                    addChargeTime,
                    latitude,
                    longitude
                )
            }
        }
    }

    data class GetParkingStatus(
        val availableSpace: Int, val usedSpace: Int, val totalSpace: Int
    ) {
        companion object {
            fun from(status: ParkingStatus) = with(status) {
                GetParkingStatus(totalParkingSpace - usedParkingSpace, usedParkingSpace, totalParkingSpace)
            }
        }
    }

    data class GetTicketByParking(
        val ticketId: Long?,
        val pkDuration: Int?,
        val price: Int?
    ) {
        companion object {
            fun from(ticket: Ticket): GetTicketByParking {
                return GetTicketByParking(ticket.id, ticket.parkingDuration, ticket.price)
            }
        }
    }
}
