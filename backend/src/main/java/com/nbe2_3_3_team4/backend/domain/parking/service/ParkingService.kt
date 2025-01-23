package com.nbe2_3_3_team4.backend.domain.parking.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.nbe2_3_3_team4.backend.domain.parking.dto.ParkingResponse.GetParking
import com.nbe2_3_3_team4.backend.domain.parking.dto.ParkingResponse.GetParkingStatus
import com.nbe2_3_3_team4.backend.domain.parking.dto.ParkingResponse.GetNearbyParking
import com.nbe2_3_3_team4.backend.domain.parking.dto.ParkingResponse.GetNearbyParking.Companion.from
import com.nbe2_3_3_team4.backend.domain.parking.dto.ParkingResponse.GetParking.Companion.from
import com.nbe2_3_3_team4.backend.domain.parking.dto.ParkingResponse.GetParkingStatus.Companion.from
import com.nbe2_3_3_team4.backend.domain.parking.entity.Parking
import com.nbe2_3_3_team4.backend.domain.parking.entity.Parking.Companion.to
import com.nbe2_3_3_team4.backend.domain.parking.entity.ParkingStatus
import com.nbe2_3_3_team4.backend.domain.parking.repository.ParkingRepository
import com.nbe2_3_3_team4.backend.domain.ticket.entity.Ticket
import com.nbe2_3_3_team4.backend.global.exception.ErrorCode
import com.nbe2_3_3_team4.backend.global.exception.NotFoundException
import lombok.RequiredArgsConstructor
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.cos

@Service
@RequiredArgsConstructor
open class ParkingService {

    @Autowired(required = true)
    private lateinit var parkingRepository: ParkingRepository

    open fun getParkingList() : List<Parking?> {
        return parkingRepository.findAll()
    }

    @Transactional(readOnly = true)
    open fun getParking(parkingId: Long): GetParking {
        val parking = parkingRepository.findById(parkingId)
            .orElseThrow { NotFoundException(ErrorCode.PKLT_NOT_FOUND) }!!

        return from(parking)
    }

    @Transactional(readOnly = true)
    open fun getParkingStatus(parkingId: Long): GetParkingStatus {
        val parking = parkingRepository.findById(parkingId)
            .orElseThrow { NotFoundException(ErrorCode.PKLT_NOT_FOUND) }!!

        return from(parking.parkingStatus!!)
    }

    fun getNearbyParking(lat: Double, lng: Double): List<GetNearbyParking> {
        println("False")
        val parkingList = getParkingList()
        println("true")
        return calculateCongestionAndSort(filterNearbyParking(parkingList, lat, lng))
    }

    private fun filterNearbyParking(parkingList: List<Parking?>, latitude: Double, longitude: Double): List<Parking?> {
        val KM = 0.5
        val lngDifference = KM / 111 / (cos(latitude))

        return parkingList.filter {
                    it?.latitude!! in (latitude - (KM / 111))..(latitude + (KM / 111)) &&
                    it.longitude!! in (longitude - lngDifference)..(longitude + lngDifference) }

    }

    private fun calculateCongestionAndSort(parkingList: List<Parking?>): List<GetNearbyParking> {
        return parkingList
            .sortedBy { pklt -> pklt?.parkingStatus?.let {
                    it.usedParkingSpace.toDouble() / it.totalParkingSpace * 100 // 주차장 혼잡도 계산
                } ?: 0.0 }
            .map { from(it!!, it.parkingStatus?.let { status ->
                    val congestionPercentage = status.usedParkingSpace.toDouble() / status.totalParkingSpace * 100
                    when {
                        congestionPercentage <= 30 -> "여유"
                        congestionPercentage <= 70 -> "보통"
                        else -> "혼잡"
                    } } ?: "정보 없음") }.toList()
    }

    @Transactional
    open fun loadDefaultDataByJson(): Void? {
        val objectMapper = ObjectMapper()
        val dataArray = objectMapper.readTree(ClassPathResource("static/data.json").file)["DATA"]

        dataArray.forEach { data ->
            parkingRepository.findByName(data["pklt_nm"].asText())?.let { // 이미 존재하는 주차장인 경우
                it.parkingStatus?.ModifyTotalParkingSpaceOfJson() // 주차장의 총 주차면수 증가
            } ?: run { // 새로운 주차장인 경우
                parkingRepository.save(to(data, ParkingStatus.to(data))) // 새로운 주차장인 경우 저장
            }
        }
        return null
    }

    @Transactional
    open fun loadDefaultTickets(): Void? {
        getParkingList().forEach { parking ->
            // 기본 요금과 기본 시간 설정 (null 혹은 0일 경우 기본값 설정)
            val basicCharge = parking?.basicCharge.takeIf { it != 0 } ?: 200
            val basicChargeTime = parking?.basicChargeTime.takeIf { it != 0 } ?: 5

            // 1시간, 2시간, 4시간, 6시간, 12시간에 대해 티켓을 생성
            intArrayOf(1, 2, 4, 6, 12).forEach { num ->
                val result = (basicCharge / basicChargeTime) * 60 * num
                parking?.regTicket(Ticket.to(parking, result, num))
            }
        }
        return null
    }
}
