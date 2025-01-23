package com.nbe2_3_3_team4.backend.controller

import com.nbe2_3_3_team4.backend.domain.parking.dto.ParkingResponse.*
import com.nbe2_3_3_team4.backend.domain.parking.service.ParkingService
import com.nbe2_3_3_team4.backend.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import lombok.extern.slf4j.Slf4j
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/parking")
@Tag(name = "🚗 Parking", description = "주차장 관련 API")
class ParkingController {

    @Autowired
    private val parkingService: ParkingService? = null

    @Operation(summary = "좌표 근처 주차장 정보 조회 API", description = "좌표 근처 주차장 정보를 조회합니다.")
    @ApiResponses(io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    @GetMapping("/search")
    fun getNearbyParking(
        @Parameter(description = "위도") @RequestParam(value = "lat") lat: Double,
        @Parameter(description = "경도") @RequestParam(value = "lng") lng: Double
    ): ResponseEntity<ApiResponse<List<GetNearbyParking>>> {
        return ResponseEntity.ok()
            .body(ApiResponse.createSuccess(parkingService!!.getNearbyParking(lat, lng)))
    }

    @Operation(summary = "주차장 조회 API", description = "주차장을 조회합니다.")
    @ApiResponses(io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    @GetMapping("/{parkingId}")
    fun getNearbyParking(
        @PathVariable parkingId: Long
    ): ResponseEntity<ApiResponse<GetParking>> {
        return ResponseEntity.ok()
            .body(ApiResponse.createSuccess(parkingService!!.getParking(parkingId)))
    }

    @Operation(summary = "주차장 잔여 자리 조회 API", description = "주차장의 잔여 자리를 조회합니다.")
    @ApiResponses(io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    @GetMapping("/{parkingId}/status")
    fun getParkingStatus(
        @PathVariable parkingId: Long
    ): ResponseEntity<ApiResponse<GetParkingStatus>> {
        return ResponseEntity.ok()
            .body(ApiResponse.createSuccess(parkingService!!.getParkingStatus(parkingId)))
    }
}
