package com.github.mkorman9.roomoccupancymanager;

import io.quarkus.test.junit.QuarkusTest;
import io.restassured.response.ValidatableResponse;
import jakarta.ws.rs.core.MediaType;
import org.assertj.core.util.Sets;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class OccupancyResourceTest {
    private static final List<BigDecimal> GUEST_OFFERS_EXAMPLE = List.of(
        BigDecimal.valueOf(23),
        BigDecimal.valueOf(45),
        BigDecimal.valueOf(155),
        BigDecimal.valueOf(374),
        BigDecimal.valueOf(22),
        BigDecimal.valueOf(99.99),
        BigDecimal.valueOf(100),
        BigDecimal.valueOf(101),
        BigDecimal.valueOf(115),
        BigDecimal.valueOf(209)
    );

    @Test
    public void testFor3PremiumAnd3Economy() {
        // given
        var offers = GUEST_OFFERS_EXAMPLE;
        var premium = 3;
        var economy = 3;

        // when
        var occupancy = sendCalculationRequest(offers, premium, economy)
            .statusCode(200)
            .extract().body().as(Occupancy.class);

        // then
        assertThat(occupancy.allocation().premium()).isEqualTo(3);
        assertThat(occupancy.allocation().economy()).isEqualTo(3);
        assertThat(occupancy.profit().total()).isEqualTo(BigDecimal.valueOf(905.99));
        assertThat(occupancy.profit().premium()).isEqualTo(BigDecimal.valueOf(738));
        assertThat(occupancy.profit().economy()).isEqualTo(BigDecimal.valueOf(167.99));
        assertRejectedReservations(occupancy,
            "could not allocate guest paying 115.00 to premium room",
            "could not allocate guest paying 101.00 to premium room",
            "could not allocate guest paying 100.00 to premium room",
            "not enough rooms to allocate guest paying 22.00"
        );
    }

    @Test
    public void testFor7PremiumAnd5Economy() {
        // given
        var offers = GUEST_OFFERS_EXAMPLE;
        var premium = 7;
        var economy = 5;

        // when
        var occupancy = sendCalculationRequest(offers, premium, economy)
            .statusCode(200)
            .extract().body().as(Occupancy.class);

        // then
        assertThat(occupancy.allocation().premium()).isEqualTo(6);
        assertThat(occupancy.allocation().economy()).isEqualTo(4);
        assertThat(occupancy.profit().total()).isEqualTo(BigDecimal.valueOf(1243.99));
        assertThat(occupancy.profit().premium()).isEqualTo(BigDecimal.valueOf(1054));
        assertThat(occupancy.profit().economy()).isEqualTo(BigDecimal.valueOf(189.99));
        assertThat(occupancy.rejectedReservations().isEmpty()).isTrue();
    }

    @Test
    public void testFor2PremiumAnd7Economy() {
        // given
        var offers = GUEST_OFFERS_EXAMPLE;
        var premium = 2;
        var economy = 7;

        // when
        var occupancy = sendCalculationRequest(offers, premium, economy)
            .statusCode(200)
            .extract().body().as(Occupancy.class);

        // then
        assertThat(occupancy.allocation().premium()).isEqualTo(2);
        assertThat(occupancy.allocation().economy()).isEqualTo(4);
        assertThat(occupancy.profit().total()).isEqualTo(BigDecimal.valueOf(772.99));
        assertThat(occupancy.profit().premium()).isEqualTo(BigDecimal.valueOf(583));
        assertThat(occupancy.profit().economy()).isEqualTo(BigDecimal.valueOf(189.99));
        assertRejectedReservations(occupancy,
            "could not allocate guest paying 155.00 to premium room",
            "could not allocate guest paying 115.00 to premium room",
            "could not allocate guest paying 101.00 to premium room",
            "could not allocate guest paying 100.00 to premium room"
        );
    }

    @Test
    public void testFor7PremiumAnd1Economy() {
        // given
        var offers = GUEST_OFFERS_EXAMPLE;
        var premium = 7;
        var economy = 1;

        // when
        var occupancy = sendCalculationRequest(offers, premium, economy)
            .statusCode(200)
            .extract().body().as(Occupancy.class);

        // then
        assertThat(occupancy.allocation().premium()).isEqualTo(7);
        assertThat(occupancy.allocation().economy()).isEqualTo(1);
        assertThat(occupancy.profit().total()).isEqualTo(BigDecimal.valueOf(1198.99));
        assertThat(occupancy.profit().premium()).isEqualTo(BigDecimal.valueOf(1153.99));
        assertThat(occupancy.profit().economy()).isEqualTo(BigDecimal.valueOf(45));
        assertRejectedReservations(occupancy,
            "not enough rooms to allocate guest paying 23.00",
            "not enough rooms to allocate guest paying 22.00"
        );
    }

    private ValidatableResponse sendCalculationRequest(List<BigDecimal> offers, int premium, int economy) {
        return given()
            .when()
            .contentType(MediaType.APPLICATION_JSON)
            .body(new OccupancyCalculationRequest(
                offers,
                new OccupancyCalculationRequest.AvailableRooms(
                    premium,
                    economy
                )
            ))
            .put("/api/occupancy")
            .then();
    }

    private void assertRejectedReservations(Occupancy occupancy, String ...causes) {
        var rejections = Sets.newHashSet(occupancy.rejectedReservations());
        assertThat(rejections).containsExactlyInAnyOrder(causes);
    }
}
