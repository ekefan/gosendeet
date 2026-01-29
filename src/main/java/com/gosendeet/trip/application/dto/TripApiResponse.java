package com.gosendeet.trip.application.dto;

import java.util.UUID;

public record TripApiResponse(
        UUID tripId,
        UUID riderId,
        UUID customerId,
        double startLat,
        double startLng,
        double endLat,
        double endLng,
        String status,
        String region,
        String travelMode,
        double plannedDistanceMeters,
        long plannedEtaSeconds,
        String startAddress,
        String endAddress
) {}
