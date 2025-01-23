package com.nbe2_3_3_team4.backend.domain.parking.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.nbe2_3_3_team4.backend.domain.parking.dto.ParkingResponse
import com.nbe2_3_3_team4.backend.domain.parking.entity.Parking
import com.nbe2_3_3_team4.backend.domain.parking.entity.ParkingStatus
import com.nbe2_3_3_team4.backend.domain.parking.repository.ParkingRepository
import com.nbe2_3_3_team4.backend.domain.ticket.entity.Ticket
import com.nbe2_3_3_team4.backend.global.exception.ErrorCode
import com.nbe2_3_3_team4.backend.global.exception.NotFoundException
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.io.IOException
import java.util.function.Function
import java.util.function.Predicate
import java.util.function.Supplier
import java.util.function.ToDoubleFunction
import kotlin.math.cos

@Service
class ParkingService(val parkingRepository: ParkingRepository) {


    @Transactional(readOnly = true)
    fun getParking(parkingId: Long): ParkingResponse.GetParking {
        val parking: Parking = parkingRepository.findById(parkingId).orElse(null) ?: throw NotFoundException(ErrorCode.PKLT_NOT_FOUND)

        return ParkingResponse.GetParking.from(parking)
    }

    @Transactional(readOnly = true)
    fun getParkingStatus(parkingId: Long): ParkingResponse.GetParkingStatus {
        val parking: Parking = parkingRepository.findById(parkingId).orElse(null) ?: throw NotFoundException(ErrorCode.PKLT_NOT_FOUND)

        return ParkingResponse.GetParkingStatus.from(parking.parkingStatus!!)
    }

    fun getNearbyParking(lat: Double, lng: Double): List<ParkingResponse.GetNearbyParking> {
        val parkingList: MutableList<Parking> = parkingList()
        val nearbyParkingList: MutableList<Parking> = filterNearbyParking(parkingList, lat, lng)
        return calculateCongestionAndSort(nearbyParkingList)
    }

    fun parkingList() : MutableList<Parking> {
         return parkingRepository.findAll() as MutableList<Parking>
    }


    fun filterNearbyParking(parkingList: MutableList<Parking>, latitude: Double, longitude: Double): MutableList<Parking> {
        val KM = 0.5
        val lngDifference = KM / 111 / (cos(latitude))

        val latFilter: Predicate<Parking> = Predicate<Parking> { parking: Parking ->
            (parking.latitude!! > latitude - (KM / 111)
                    && latitude + (KM / 111) > parking.latitude!!)
        }
        val lngFilter: Predicate<Parking> = Predicate<Parking> { parking: Parking ->
            (parking.longitude!! > longitude - lngDifference
                    && parking.longitude!! < longitude + lngDifference)
        }

        return parkingList.stream()
                .filter(latFilter.and(lngFilter))
                .toList()
    }

    private fun calculateCongestionAndSort(parkingList: List<Parking>): List<ParkingResponse.GetNearbyParking> {
        return parkingList.stream().sorted(
                Comparator.comparingDouble { pklt: Parking ->
                    val status: ParkingStatus? = pklt.parkingStatus
                    ((status?.usedParkingSpace ?: 1) / (status?.totalParkingSpace ?: 1) * 100).toDouble()
                })
                .map { pklt: Parking ->
                    val status: ParkingStatus? = pklt.parkingStatus
                    val percentage: Double = (status?.usedParkingSpace?.toDouble()
                            ?: 1.0) / status?.totalParkingSpace!! * 100
                    val congestionStatus = if (percentage <= 30) {
                        "여유"
                    } else if (percentage <= 70) {
                        "보통"
                    } else {
                        "혼잡"
                    }
                    ParkingResponse.GetNearbyParking.from(pklt, congestionStatus)
                }.toList()
    }

    @Transactional
    @Throws(IOException::class)
    fun loadDefaultDataByJson(){
        val objectMapper = ObjectMapper()
        

        val root = objectMapper.readTree(ClassPathResource("static/data.json").file)
        val dataArray = root["DATA"]


        for (data in dataArray) {
            val parkingName = data["pklt_nm"].asText()

            val parking: Parking? = parkingRepository.findByName(parkingName)

            if (parking == null) {
                parkingRepository.save(Parking.to(data, ParkingStatus.to(data)))
            } else {
                parking.parkingStatus?.modifyTotalParkingSpaceOfJson()
            }
        }
    }

    @Transactional
    fun loadDefaultTickets(): Unit {
        val parkingList: MutableList<Parking?> = parkingRepository.findAll()
        val nums = intArrayOf(1, 2, 4, 6, 12)

        for (parking in parkingList) {
            // 1시간 2시간 4시간 6시간 12시간
            // bsc_prk_crg => 기본 요금
            // bsc_prk_hr => 분단위

            for (num in nums) {
                var basicCharge: Int? = parking?.basicCharge
                var basicChargeTime: Int? = parking?.basicChargeTime

                if (basicCharge == 0) {
                    basicCharge = 220
                    basicChargeTime = 5
                }

                val result = ((basicCharge?.div(basicChargeTime!!))?.times(60) ?: 1) * num

                parking?.regTicket(Ticket.to(parking, result, num))
            }
        }

    }
}
