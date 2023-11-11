package com.github.mkorman9.roomoccupancymanager;

import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class OccupancyCalculator {
    private static final BigDecimal PREMIUM_ROOM_PRICE_BOUNDARY = BigDecimal.valueOf(100);

    public Occupancy calculate(List<BigDecimal> guestOffers, int premiumRoomsAvailable, int economyRoomsAvailable) {
        var offersPartitionedByCategory = guestOffers.stream()
            .sorted(Collections.reverseOrder())
            .collect(Collectors.partitioningBy(this::requiresPremiumRoom));
        var premiumOffers = offersPartitionedByCategory.get(true);
        var economyOffers = offersPartitionedByCategory.get(false);

        var premiumRoomsAssigner = new PremiumRoomsAssigner();
        var economyRoomsAssigner = new EconomyRoomsAssigner();
        premiumRoomsAssigner.assign(premiumOffers, premiumRoomsAvailable);
        economyRoomsAssigner.assign(economyOffers, premiumRoomsAssigner.getPremiumRoomsLeft(), economyRoomsAvailable);

        return new Occupancy(
            new Occupancy.Allocation(
                premiumRoomsAssigner.getPremiumRoomsAllocated() + economyRoomsAssigner.getPremiumRoomsUpgraded(),
                economyRoomsAssigner.getEconomyRoomsAllocated()
            ),
            new Occupancy.Profit(
                premiumRoomsAssigner.getProfit().add(economyRoomsAssigner.getTotalProfit()),
                premiumRoomsAssigner.getProfit().add(economyRoomsAssigner.getPremiumUpgradesProfit()),
                economyRoomsAssigner.getEconomyRoomsProfit()
            ),
            Stream.concat(
                premiumRoomsAssigner.getRejectedReservations().stream(),
                economyRoomsAssigner.getRejectedReservations().stream()
            ).toList()
        );
    }

    private boolean requiresPremiumRoom(BigDecimal offer) {
        return offer.compareTo(PREMIUM_ROOM_PRICE_BOUNDARY) >= 0;
    }
}
