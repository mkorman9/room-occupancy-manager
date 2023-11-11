package com.github.mkorman9.roomoccupancymanager;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PremiumRoomsAssignerTest {
    @Test
    public void testFit2GuestsIn3Rooms() {
        // given
        var offers = List.of(BigDecimal.valueOf(120), BigDecimal.valueOf(110));
        var roomsAvailable = 3;
        var assigner = new PremiumRoomsAssigner();

        // when
        assigner.assign(offers, roomsAvailable);

        // then
        assertThat(assigner.getPremiumRoomsAllocated()).isEqualTo(2);
        assertThat(assigner.getPremiumRoomsLeft()).isEqualTo(1);
        assertThat(assigner.getProfit()).isEqualTo(BigDecimal.valueOf(230));
        assertThat(assigner.getRejectedReservations().isEmpty()).isTrue();
    }

    @Test
    public void testFit3GuestsIn2Rooms() {
        // given
        var offers = List.of(BigDecimal.valueOf(130), BigDecimal.valueOf(120), BigDecimal.valueOf(110));
        var roomsAvailable = 2;
        var assigner = new PremiumRoomsAssigner();

        // when
        assigner.assign(offers, roomsAvailable);

        // then
        assertThat(assigner.getPremiumRoomsAllocated()).isEqualTo(2);
        assertThat(assigner.getPremiumRoomsLeft()).isEqualTo(0);
        assertThat(assigner.getProfit()).isEqualTo(BigDecimal.valueOf(250));
        assertThat(assigner.getRejectedReservations().size()).isEqualTo(1);
        assertThat(assigner.getRejectedReservations().get(0)).isEqualTo(
            "could not allocate guest paying 110.00 to premium room"
        );
    }
}
