package com.nbe2_3_3_team4.backend.controller;

import com.nbe2_3_3_team4.backend.domain.car.dto.CarRequest;
import com.nbe2_3_3_team4.backend.domain.car.dto.CarResponse;
import com.nbe2_3_3_team4.backend.domain.car.service.CarService;
import com.nbe2_3_3_team4.backend.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cars")
@Tag(name = "🚜 Car", description = "차량 관련 API")
public class CarController {

	private final CarService carService;

	@Operation(summary = "차량 등록 API", description = "차량 정보를 등록합니다.")
	@ApiResponses({@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공")})
	@PostMapping
	public ResponseEntity<ApiResponse<CarResponse.RegCar>> registerCar(
		@AuthenticationPrincipal User user, @RequestBody CarRequest.RegCar dto) {
		return ResponseEntity.status(HttpStatus.CREATED)
			.body(ApiResponse.createSuccess(carService.registerCar(dto, user.getUsername())));
	}

	@Operation(summary = "대표 차량 수정 API", description = "대표 차량 등록 또는 해제를 처리합니다.")
	@ApiResponses({@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공")})
	@PutMapping("/{carId}/primary")
	public ResponseEntity<ApiResponse<Void>> registerCarPrimary(
		@PathVariable Long carId) {
		return ResponseEntity.ok()
			.body(ApiResponse.createSuccess(carService.updatePrimary(carId)));
	}

	@Operation(summary = "회원 차량 목록 조회 API", description = "차량 목록을 조회합니다.")
	@ApiResponses({@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "성공")})
	@GetMapping
	public ResponseEntity<ApiResponse<List<CarResponse.GetCar>>> getCars(
		@AuthenticationPrincipal User user) {
		return ResponseEntity.ok()
			.body(ApiResponse.createSuccess(carService.getCars(user.getUsername())));
	}

	@Operation(summary = "차량 정보 수정 API", description = "차량 정보를 수정합니다.")
	@ApiResponses({@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공")})
	@PutMapping("/{carId}")
	public ResponseEntity<ApiResponse<Void>> updateCar(@PathVariable Long carId, @RequestBody CarRequest.modify dto) {
		return ResponseEntity.ok()
			.body(ApiResponse.createSuccess(carService.modify(carId, dto)));
	}

	@Operation(summary = "차량 삭제 API", description = "차량을 삭제합니다.")
	@ApiResponses({@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "성공")})
	@DeleteMapping("/{carId}")
	public ResponseEntity<ApiResponse<Void>> deleteCar(@PathVariable Long carId) {
		return ResponseEntity.ok()
			.body(ApiResponse.createSuccess(carService.delete(carId)));
	}

}
