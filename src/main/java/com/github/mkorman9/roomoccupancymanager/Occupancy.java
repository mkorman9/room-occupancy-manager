package com.github.mkorman9.roomoccupancymanager;

import java.math.BigDecimal;
import java.util.List;

public record Occupancy(
    Allocation allocation,
    Profit profit,
    List<String> rejectedReservations
) {
    public record Allocation(
        int premium,
        int economy
    ) {
    }

    public record Profit(
        BigDecimal total,
        BigDecimal premium,
        BigDecimal economy
    ) {
    }
}
