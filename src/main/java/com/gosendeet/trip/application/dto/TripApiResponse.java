package com.gosendeet.trip.application.dto;

import java.util.UUID;

public record TripApiResponse(
        UUID tripId,
        UUID riderId,
        UUID customerId,
        double pickupLat,
        double pickupLng,
        double destinationLat,
        double destinationLng,
        String status
) {}
