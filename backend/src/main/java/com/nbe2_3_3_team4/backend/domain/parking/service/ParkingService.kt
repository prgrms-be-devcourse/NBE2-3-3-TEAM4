package com.nbe2_3_3_team4.backend.domain.parking.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.nbe2_3_3_team4.backend.domain.parking.dto.ParkingResponse
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
import org.springframework.core.io.ClassPathResource
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.math.cos

const val KM = 0.5
const val latKm = KM / 111
const val defaultCharge = 200
const val defaultChargeTime = 5
val lowCongestion = 0.0..30.0 // 혼잡도 범위 여유
val mediumCongestion = 30.0..70.0 // 혼잡도 범위 보통

@Service
class ParkingService( private val parkingRepository: ParkingRepository ) {

    @get:Transactional(readOnly = true)
    val parkingList: List<Parking?> get() = parkingRepository.findAll().filterNotNull() // 주차장 목록 조회

    @Transactional(readOnly = true)
    fun getParking(parkingId: Long): GetParking = from( parkingRepository.findById(parkingId) // 주차장 조회
            .orElseThrow { NotFoundException(ErrorCode.PKLT_NOT_FOUND) }!! ) // 예외 처리

    @Transactional(readOnly = true)
    fun getParkingStatus(parkingId: Long): GetParkingStatus = from( parkingRepository.findById(parkingId) // 주차장 상태 조회
            .orElseThrow { NotFoundException(ErrorCode.PKLT_NOT_FOUND) }!!.parkingStatus!! ) // 예외 처리

    @Transactional(readOnly = true)
    fun getNearbyParking(lat: Double, lng: Double): List<GetNearbyParking> = calculateCongestionAndSort(filterNearbyParking(parkingList, lat, lng)) // 주변 주차장 조회

    private fun filterNearbyParking(parkingList: List<Parking?>, latitude: Double, longitude: Double): List<Parking?> {
        val lngDifference = latKm / (cos(latitude)) // 경도 차이 계산

        return parkingList.filterNotNull().filter {
                    it.latitude != null && it.longitude != null &&
                    it.latitude!! in (latitude - latKm)..(latitude + latKm) &&
                    it.longitude!! in (longitude - lngDifference)..(longitude + lngDifference) } // 주변 주차장 필터링
    }

    private fun calculateCongestionAndSort(parkingList: List<Parking?>): List<GetNearbyParking> {
        return parkingList.filterNotNull()
            .sortedBy { it.parkingStatus?.getCongestionRate() ?: 0.0 } // 주차장 혼잡도에 따라 정렬
            .map { from(it, it.parkingStatus?.let { status ->
                        when (status.getCongestionRate()) {
                            in lowCongestion -> "여유"
                            in mediumCongestion -> "보통"
                            else -> "혼잡"
                        } } ?: "정보 없음") } // 주차장 혼잡도를 3단계로 나누어 반환
    }

    private fun ParkingStatus.getCongestionRate(): Double = usedParkingSpace.toDouble() / totalParkingSpace * 100

    @Transactional
    fun loadDefaultDataByJson() {
        val objectMapper = ObjectMapper()
        val dataArray = objectMapper.readTree(ClassPathResource("static/data.json").file)["DATA"]

        dataArray.forEach { data ->
            parkingRepository.findByName(data["pklt_nm"].asText())?.let { // 이미 존재하는 주차장인 경우
                it.parkingStatus?.modifyTotalParkingSpaceOfJson() // 주차장의 총 주차면수 증가
            } ?: run { parkingRepository.save(to(data, ParkingStatus.to(data))) } } // 새로운 주차장인 경우 저장
    }

    @Transactional
    fun loadDefaultTickets() {
        parkingList.forEach { parking ->
            val basicCharge = parking?.basicCharge?.takeIf { it != 0 } ?: defaultCharge // 기본 요금 200원
            val basicChargeTime = parking?.basicChargeTime?.takeIf { it != 0 } ?: defaultChargeTime // 기본 요금 시간 5분

            intArrayOf(1, 2, 4, 6, 12).forEach { num -> // 1, 2, 4, 6, 12시간 주차권 생성
                val result = (basicCharge / basicChargeTime) * 60 * num
                parking?.regTicket(Ticket.to(parking, result, num))
            } }
    }

    @Transactional
    fun getTicketsByParking(parkingId: Long) : List<ParkingResponse.GetTicketByParking>{
        val parking: Parking =
            parkingRepository.findById(parkingId)
                .orElseThrow{ NotFoundException(ErrorCode.PKLT_NOT_FOUND) }!!

        val tickets: MutableList<Ticket> = parking.tickets

        val response: MutableList<ParkingResponse.GetTicketByParking> = ArrayList()
        for (ticket in tickets) {
            response.add(ParkingResponse.GetTicketByParking.from(ticket))
        }

        return response
    }
}