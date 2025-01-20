package com.nbe2_3_3_team4.backend.domain.parking.dto;

import com.nbe2_3_3_team4.backend.domain.parking.entity.Parking;
import com.nbe2_3_3_team4.backend.domain.parking.entity.ParkingStatus;

public record ParkingResponse() {

    public record GetNearbyParking(Long id, String name, Double latitude, Double longitude, String status) {
        public static GetNearbyParking from(Parking parking, String status) {
            return new GetNearbyParking(parking.getParkingId(), parking.getName(), parking.getLatitude(), parking.getLongitude(), status);
        }
    }

    public record GetParking(String name, String address, String weOpenTime, String weEndTime,
                             String wdOpenTime, String wdEndTime, int basicCharge, int addCharge, int addChargeTime) {
        public static GetParking from(Parking parking) {
            return new GetParking(parking.getName(), parking.getAddress(),
                parking.getWeOpenTime(), parking.getWeEndTime(), parking.getWdOpenTime(), parking.getWdEndTime(),
                parking.getBasicCharge(), parking.getAddCharge(), parking.getAddChargeTime());
        }
    }

    public record GetParkingStatus(int availableSpace,
                                   int usedSpace,
                                   int totalSpace) {
        public static GetParkingStatus from(ParkingStatus status) {
            return new GetParkingStatus(
                status.getTotalParkingSpace() - status.getUsedParkingSpace(), status.getUsedParkingSpace(), status.getTotalParkingSpace());
        }
    }
}
