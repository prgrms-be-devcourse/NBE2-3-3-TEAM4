package com.nbe2_3_3_team4.backend.domain.parking.dto

import com.nbe2_3_3_team4.backend.domain.order.entity.Order
import com.nbe2_3_3_team4.backend.domain.order.entity.enum.OrderStatus
import com.nbe2_3_3_team4.backend.domain.parking.entity.Parking
import com.nbe2_3_3_team4.backend.domain.parking.entity.ParkingStatus
import java.time.format.DateTimeFormatter

class ParkingResponse {
    data class GetNearbyParking(val id: Long?, val name: String?, val latitude: Double?, val longitude: Double?, val status: String?) {
        companion object {
            fun from(parking: Parking, status: String) = with(parking) {
                GetNearbyParking(parkingId, name, latitude, longitude, status)
            }
        }
    }

    data class GetParking(
        val name: String?, val address: String?, val weOpenTime: String?, val weEndTime: String?,
        val wdOpenTime: String?, val wdEndTime: String?, val basicCharge: Int, val addCharge: Int, val addChargeTime: Int
    ) {
        companion object {
            fun from(parking: Parking) = with(parking) {
                GetParking( name, address, weOpenTime, weEndTime, wdOpenTime, wdEndTime, basicCharge, addCharge, addChargeTime )
            }
        }
    }

    data class GetParkingStatus(val availableSpace: Int, val usedSpace: Int, val totalSpace: Int) {
        companion object {
            fun from(status: ParkingStatus) = with(status) {
                GetParkingStatus( totalParkingSpace - usedParkingSpace, usedParkingSpace, totalParkingSpace )
            }
        }
    }

    data class GetEnterParking(val orderId: String, val enterTime: String, val expectedExitTime: String, val carNumber: String, val status: OrderStatus, val orderTime: String) {
        companion object {
            fun from(order: Order) = with(order) {
                val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                GetEnterParking(
                    id,
                    orderDetail.startParkingTime!!.format(timeFormatter),
                    orderDetail.startParkingTime!!.plusHours(ticket.parkingDuration!!.toLong()).plusMinutes(10).format(timeFormatter),
                    orderDetail.carNumber,
                    orderStatus,
                    createdAt!!.format(timeFormatter)
                )
            }
        }
    }

    data class GetExitParking(val orderId: String, val enterTime: String, val exitTime: String, val carNumber: String, val status: OrderStatus, val orderTime: String, val addPrice: Int?, val totalPrice: Int) {
        companion object {
            fun from(order: Order) = with(order) {
                val timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
                GetExitParking(
                    id,
                    orderDetail.startParkingTime!!.format(timeFormatter),
                    orderDetail.endParkingTime!!.format(timeFormatter),
                    orderDetail.carNumber,
                    orderStatus,
                    createdAt!!.format(timeFormatter),
                    orderDetail.addPrice,
                    orderDetail.totalPrice
                )
            }
        }
    }
}
