package com.github.mkorman9.roomoccupancymanager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class PremiumRoomsAssigner {
    private int premiumRoomsLeft = 0;
    private int premiumRoomsAllocated = 0;
    private BigDecimal profit = BigDecimal.ZERO;
    private final List<String> rejectedReservations = new ArrayList<>();

    public void assign(List<BigDecimal> premiumOffers, int premiumRoomsAvailable) {
        premiumRoomsLeft += premiumRoomsAvailable;

        for (var offer : premiumOffers) {
            if (premiumRoomsLeft == 0) {
                rejectedReservations.add(
                    String.format("could not allocate guest paying %.2f to premium room", offer)
                );
                continue;
            }

            premiumRoomsLeft--;
            profit = profit.add(offer);
        }

        premiumRoomsAllocated += premiumRoomsAvailable - premiumRoomsLeft;
    }

    public int getPremiumRoomsLeft() {
        return premiumRoomsLeft;
    }

    public int getPremiumRoomsAllocated() {
        return premiumRoomsAllocated;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public List<String> getRejectedReservations() {
        return rejectedReservations;
    }
}
