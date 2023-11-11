package com.github.mkorman9.roomoccupancymanager;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class EconomyRoomsAssignerTest {
    @Test
    public void testFit2GuestsIn3EconomyRooms() {
        // given
        var offers = List.of(BigDecimal.valueOf(50), BigDecimal.valueOf(40));
        var premiumRoomsAvailable = 0;
        var economyRoomsAvailable = 3;
        var assigner = new EconomyRoomsAssigner();

        // when
        assigner.assign(offers, premiumRoomsAvailable, economyRoomsAvailable);

        // then
        assertThat(assigner.getEconomyRoomsAllocated()).isEqualTo(2);
        assertThat(assigner.getPremiumRoomsUpgraded()).isEqualTo(0);
        assertThat(assigner.getTotalProfit()).isEqualTo(BigDecimal.valueOf(90));
        assertThat(assigner.getPremiumUpgradesProfit()).isEqualTo(BigDecimal.ZERO);
        assertThat(assigner.getEconomyRoomsProfit()).isEqualTo(BigDecimal.valueOf(90));
        assertThat(assigner.getRejectedReservations().isEmpty()).isTrue();
    }

    @Test
    public void testFit3GuestsIn2EconomyRooms() {
        // given
        var offers = List.of(BigDecimal.valueOf(60), BigDecimal.valueOf(50), BigDecimal.valueOf(40));
        var premiumRoomsAvailable = 0;
        var economyRoomsAvailable = 2;
        var assigner = new EconomyRoomsAssigner();

        // when
        assigner.assign(offers, premiumRoomsAvailable, economyRoomsAvailable);

        // then
        assertThat(assigner.getEconomyRoomsAllocated()).isEqualTo(2);
        assertThat(assigner.getPremiumRoomsUpgraded()).isEqualTo(0);
        assertThat(assigner.getTotalProfit()).isEqualTo(BigDecimal.valueOf(110));
        assertThat(assigner.getPremiumUpgradesProfit()).isEqualTo(BigDecimal.ZERO);
        assertThat(assigner.getEconomyRoomsProfit()).isEqualTo(BigDecimal.valueOf(110));
        assertThat(assigner.getRejectedReservations().get(0)).isEqualTo(
            "not enough rooms to allocate guest paying 40.00"
        );
    }

    @Test
    public void testFit2GuestsIn1EconomyAnd1PremiumRoom() {
        // given
        var offers = List.of(BigDecimal.valueOf(60), BigDecimal.valueOf(50));
        var premiumRoomsAvailable = 1;
        var economyRoomsAvailable = 1;
        var assigner = new EconomyRoomsAssigner();

        // when
        assigner.assign(offers, premiumRoomsAvailable, economyRoomsAvailable);

        // then
        assertThat(assigner.getEconomyRoomsAllocated()).isEqualTo(1);
        assertThat(assigner.getPremiumRoomsUpgraded()).isEqualTo(1);
        assertThat(assigner.getTotalProfit()).isEqualTo(BigDecimal.valueOf(110));
        assertThat(assigner.getPremiumUpgradesProfit()).isEqualTo(BigDecimal.valueOf(60));
        assertThat(assigner.getEconomyRoomsProfit()).isEqualTo(BigDecimal.valueOf(50));
        assertThat(assigner.getRejectedReservations().isEmpty()).isTrue();
    }
}
