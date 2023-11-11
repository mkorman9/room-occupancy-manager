package com.github.mkorman9.roomoccupancymanager;

import java.math.BigDecimal;
import java.util.List;

public record OccupancyCalculationRequest(
    List<BigDecimal> guestOffers,
    AvailableRooms availableRooms
) {
    public record AvailableRooms(
       int premium,
       int economy
    ) {
    }
}
