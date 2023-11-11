package com.github.mkorman9.roomoccupancymanager;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public record OccupancyCalculationRequest(
    @NotNull List<@Min(0) BigDecimal> guestOffers,
    @NotNull @Valid AvailableRooms availableRooms
) {
    public record AvailableRooms(
       @Min(0) int premium,
       @Min(0) int economy
    ) {
    }
}
