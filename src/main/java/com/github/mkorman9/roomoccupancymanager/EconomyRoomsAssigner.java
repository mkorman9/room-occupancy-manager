package com.github.mkorman9.roomoccupancymanager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class EconomyRoomsAssigner {
    private int premiumRoomsUpgraded = 0;
    private int economyRoomsAllocated = 0;
    private BigDecimal premiumRoomsProfit = BigDecimal.ZERO;
    private BigDecimal economyRoomsProfit = BigDecimal.ZERO;
    private final List<String> rejectedReservations = new ArrayList<>();

    public void assign(List<BigDecimal> economyOffers, int premiumRoomsAvailable, int economyRoomsAvailable) {
        var premiumRoomsLeft = premiumRoomsAvailable;
        var economyRoomsLeft = economyRoomsAvailable;

        for (var offer : economyOffers) {
            // upgrade if there are premium rooms left and we can't fit everyone in the economy
            if (premiumRoomsLeft > 0 && economyOffers.size() > economyRoomsAvailable) {
                premiumRoomsLeft--;
                premiumRoomsProfit = premiumRoomsProfit.add(offer);
                continue;
            }

            if (economyRoomsLeft == 0) {
                rejectedReservations.add(
                    String.format("not enough rooms to allocate guest paying %.2f", offer)
                );
                continue;
            }

            economyRoomsLeft--;
            economyRoomsProfit = economyRoomsProfit.add(offer);
        }

        premiumRoomsUpgraded += premiumRoomsAvailable - premiumRoomsLeft;
        economyRoomsAllocated += economyRoomsAvailable - economyRoomsLeft;
    }

    public int getPremiumRoomsUpgraded() {
        return premiumRoomsUpgraded;
    }

    public int getEconomyRoomsAllocated() {
        return economyRoomsAllocated;
    }

    public BigDecimal getPremiumUpgradesProfit() {
        return premiumRoomsProfit;
    }

    public BigDecimal getEconomyRoomsProfit() {
        return economyRoomsProfit;
    }

    public BigDecimal getTotalProfit() {
        return premiumRoomsProfit.add(economyRoomsProfit);
    }

    public List<String> getRejectedReservations() {
        return rejectedReservations;
    }
}
