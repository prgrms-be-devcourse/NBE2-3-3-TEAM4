package com.nbe2_3_3_team4.backend.controller

import com.nbe2_3_3_team4.backend.domain.car.dto.CarRequest
import com.nbe2_3_3_team4.backend.domain.car.dto.CarResponse
import com.nbe2_3_3_team4.backend.domain.car.dto.CarResponse.GetCar
import com.nbe2_3_3_team4.backend.domain.car.service.CarService
import com.nbe2_3_3_team4.backend.global.response.ApiResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/cars")
@Tag(name = "🚜 Car", description = "차량 관련 API")
class CarController(val carService: CarService) {


    @Operation(summary = "차량 등록 API", description = "차량 정보를 등록합니다.")
    @ApiResponses(io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공"))
    @PostMapping
    fun registerCar(
            @AuthenticationPrincipal user: User, @RequestBody dto: CarRequest.RegCar?): ResponseEntity<ApiResponse<CarResponse.RegCar>> {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.createSuccess(carService.registerCar(dto!!, user.username)))
    }

    @Operation(summary = "대표 차량 수정 API", description = "대표 차량 등록 또는 해제를 처리합니다.")
    @ApiResponses(io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공"))
    @PutMapping("/{carId}/primary")
    fun registerCarPrimary(
            @PathVariable carId: Long?): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok()
                .body(ApiResponse.createSuccess(carService.updatePrimary(carId!!)))
    }

    @Operation(summary = "회원 차량 목록 조회 API", description = "차량 목록을 조회합니다.")
    @ApiResponses(io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공"))
    @GetMapping
    fun getCars(
            @AuthenticationPrincipal user: User): ResponseEntity<ApiResponse<List<GetCar>>> {
        return ResponseEntity.ok()
                .body(ApiResponse.createSuccess(carService.getCars(user.username)))
    }

    @Operation(summary = "차량 정보 수정 API", description = "차량 정보를 수정합니다.")
    @ApiResponses(io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공"))
    @PutMapping("/{carId}")
    fun updateCar(@PathVariable carId: Long?, @RequestBody dto: CarRequest.Modify?): ResponseEntity<ApiResponse<Unit>> {
        return ResponseEntity.ok()
                .body(ApiResponse.createSuccess(carService.modify(carId!!, dto!!)))
    }

    @Operation(summary = "차량 삭제 API", description = "차량을 삭제합니다.")
    @ApiResponses(io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공"))
    @DeleteMapping("/{carId}")
    fun deleteCar(@PathVariable carId: Long?): ResponseEntity<ApiResponse<Void?>> {
        return ResponseEntity.ok()
                .body(ApiResponse.createSuccess(carService.delete(carId!!)))
    }
}
