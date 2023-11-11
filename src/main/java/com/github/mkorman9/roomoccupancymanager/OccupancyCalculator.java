package com.github.mkorman9.roomoccupancymanager;

import jakarta.enterprise.context.ApplicationScoped;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class OccupancyCalculator {
    private static final BigDecimal PREMIUM_ROOM_PRICE_BOUNDARY = BigDecimal.valueOf(100);

    public Occupancy calculate(List<BigDecimal> guestOffers, int premiumRooms, int economyRooms) {
        var offers = guestOffers.stream()
            .sorted(Collections.reverseOrder())
            .collect(Collectors.partitioningBy(this::requiresPremiumRoom));
        var offersRequiringPremium = offers.get(true);
        var economyOffers = offers.get(false);

        var premiumRoomsLeft = premiumRooms;
        var economyRoomsLeft = economyRooms;
        var premiumRoomsProfit = BigDecimal.ZERO;
        var economyRoomsProfit = BigDecimal.ZERO;
        var rejectedReservations = new ArrayList<String>();

        for (var offer : offersRequiringPremium) {
            if (premiumRoomsLeft == 0) {
                rejectedReservations.add(
                    String.format("could not allocate guest paying %.2f to premium room", offer)
                );
                continue;
            }

            premiumRoomsLeft--;
            premiumRoomsProfit = premiumRoomsProfit.add(offer);
        }

        for (var offer : economyOffers) {
            if (premiumRoomsLeft > 0 && economyOffers.size() > economyRooms) {
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

        return new Occupancy(
            new Occupancy.Allocation(
                premiumRooms - premiumRoomsLeft,
                economyRooms - economyRoomsLeft
            ),
            new Occupancy.Profit(
                premiumRoomsProfit.add(economyRoomsProfit),
                premiumRoomsProfit,
                economyRoomsProfit
            ),
            rejectedReservations
        );
    }

    private boolean requiresPremiumRoom(BigDecimal offer) {
        return offer.compareTo(PREMIUM_ROOM_PRICE_BOUNDARY) >= 0;
    }
}
