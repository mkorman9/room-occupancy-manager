package com.github.mkorman9.roomoccupancymanager;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/occupancy")
public class OccupancyResource {
    @PUT
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Occupancy calculateOccupancy(OccupancyCalculationRequest request) {
        return null;
    }
}
