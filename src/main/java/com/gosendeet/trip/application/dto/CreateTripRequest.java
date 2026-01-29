package com.gosendeet.trip.application.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CreateTripRequest(

    @NotNull(message = "customerId is required")
    UUID customerId,

    @NotNull(message = "startLat is required")
    Double startLat,

    @NotNull(message = "startLng is required")
    Double startLng,

    @NotNull(message = "endLat is required")
    Double endLat,

    @NotNull(message = "endLng is required")
    Double endLng,

    String startAddress,
    String endAddress,
    String region,
    String travelMode,

    @NotNull(message = "plannedDistanceMeters is required")
    @Positive(message = "plannedDistanceMeters must be positive")
    double plannedDistanceMeters,

    @NotNull(message = "plannedEtaSeconds is required")
    @Positive(message = "plannedEtaSeconds must be positive")
    double plannedEtaSeconds
) {}
