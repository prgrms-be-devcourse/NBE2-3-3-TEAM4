package com.nbe2_3_3_team4.backend.domain.parking.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.nbe2_3_3_team4.backend.domain.member.repository.MemberRepository
import com.nbe2_3_3_team4.backend.domain.order.repository.OrderDetailRepository
import com.nbe2_3_3_team4.backend.domain.order.repository.OrderRepository
import com.nbe2_3_3_team4.backend.domain.order.entity.enum.OrderStatus.*
import com.nbe2_3_3_team4.backend.domain.parking.dto.ParkingResponse.GetParking
import com.nbe2_3_3_team4.backend.domain.parking.dto.ParkingResponse.GetParkingStatus
import com.nbe2_3_3_team4.backend.domain.parking.dto.ParkingResponse.GetNearbyParking
import com.nbe2_3_3_team4.backend.domain.parking.dto.ParkingResponse.GetEnterParking
import com.nbe2_3_3_team4.backend.domain.parking.dto.ParkingResponse.GetNearbyParking.Companion.from
import com.nbe2_3_3_team4.backend.domain.parking.dto.ParkingResponse.GetParking.Companion.from
import com.nbe2_3_3_team4.backend.domain.parking.dto.ParkingResponse.GetParkingStatus.Companion.from
import com.nbe2_3_3_team4.backend.domain.parking.dto.ParkingResponse.GetEnterParking.Companion.from
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
import java.time.Duration
import kotlin.math.cos

const val KM = 0.5
const val latKm = KM / 111
const val defaultCharge = 200
const val defaultChargeTime = 5
val lowCongestion = 0.0..30.0 // 혼잡도 범위 여유
val mediumCongestion = 30.0..70.0 // 혼잡도 범위 보통

@Service
class ParkingService(
    private val parkingRepository: ParkingRepository,
    private val memberRepository: MemberRepository,
    private val orderRepository: OrderRepository,
    private val orderDetailRepository: OrderDetailRepository,
) {

    // 주차장 목록 조회
    @get:Transactional(readOnly = true)
    val parkingList: List<Parking?> get() = parkingRepository.findAll().filterNotNull()

    // 주차장 생성
    @Transactional(readOnly = true)
    fun getParking(parkingId: Long): GetParking = from( parkingRepository.findById(parkingId)
            .orElseThrow { NotFoundException(ErrorCode.PKLT_NOT_FOUND) }!! ) // 예외 처리

    // 주차장 상태 조회
    @Transactional(readOnly = true)
    fun getParkingStatus(parkingId: Long): GetParkingStatus = from( parkingRepository.findById(parkingId)
            .orElseThrow { NotFoundException(ErrorCode.PKLT_NOT_FOUND) }!!.parkingStatus!! ) // 예외 처리

    // 주변 주차장 조회
    @Transactional(readOnly = true)
    fun getNearbyParking(lat: Double, lng: Double): List<GetNearbyParking> = calculateCongestionAndSort(filterNearbyParking(parkingList, lat, lng))

    // 주변 주차장 필터링
    private fun filterNearbyParking(parkingList: List<Parking?>, latitude: Double, longitude: Double): List<Parking?> {
        val lngDifference = latKm / (cos(latitude)) // 경도 차이 계산

        return parkingList.filterNotNull().filter {
                    it.latitude != null && it.longitude != null &&
                    it.latitude!! in (latitude - latKm)..(latitude + latKm) &&
                    it.longitude!! in (longitude - lngDifference)..(longitude + lngDifference) } // 주변 주차장 필터링
    }

    // 혼잡도 계산 및 정렬
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

    // 혼잡도 계산
    private fun ParkingStatus.getCongestionRate(): Double = usedParkingSpace.toDouble() / totalParkingSpace * 100

    @Transactional
    fun enterParking(email: String, carNumber: String): GetEnterParking {
        val member = memberRepository.findByEmail(email).orElseThrow { NotFoundException(ErrorCode.USER_NOT_FOUND) }
        val order = orderRepository.findAllByMemberAndOrderStatusAndOrderDetail_CarNumber(member, WAITING, carNumber).orElseThrow { NotFoundException(ErrorCode.ORDER_NOT_FOUND) }[0]
        val orderDetail = order.orderDetail

        // 입차 상태 업데이트
        order.updateOrderStatus(PARKING)
        orderDetail.updateStartParkingTime(order.updatedAt!!)

        return from(order)
    }

    @Transactional
    fun exitParking(email: String, carNumber: String) {
        val member = memberRepository.findByEmail(email).orElseThrow { NotFoundException(ErrorCode.USER_NOT_FOUND) }
        val order = orderRepository.findAllByMemberAndOrderStatusAndOrderDetail_CarNumber(member, PARKING, carNumber).orElseThrow { NotFoundException(ErrorCode.ORDER_NOT_FOUND) }[0]
        val orderDetail = order.orderDetail

        // 출차 상태 업데이트
        order.updateOrderStatus(EXIT)
        orderDetail.updateEndParkingTime(order.updatedAt!!)

        // 주차 요금 계산
        val parkingTime = Duration.between(orderDetail.startParkingTime, orderDetail.endParkingTime).toMinutes()
        val ticket = order.ticket

        // 주차 현황 주차 차량수 감소
        order.ticket.parking?.parkingStatus?.updateUsedParkingSpace()
    }

    // Default Parking Lot Data Load
    @Transactional
    fun loadDefaultDataByJson() {
        val objectMapper = ObjectMapper()
        val dataArray = objectMapper.readTree(ClassPathResource("static/data.json").file)["DATA"]

        dataArray.forEach { data ->
            parkingRepository.findByName(data["pklt_nm"].asText())?.let { // 이미 존재하는 주차장인 경우
                it.parkingStatus?.ModifyTotalParkingSpaceOfJson() // 주차장의 총 주차면수 증가
            } ?: run { parkingRepository.save(to(data, ParkingStatus.to(data))) } } // 새로운 주차장인 경우 저장
    }

    // Default Ticket Data Load
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
}