package com.gosendeet.trip.application.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record CreateTripRequest(

    @NotNull(message = "riderId is required")
    UUID riderId,

    @NotNull(message = "customerId is required")
    UUID customerId,

    @NotNull(message = "pickupLat is required")
    Double pickupLat,

    @NotNull(message = "pickupLng is required")
    Double pickupLng,

    @NotNull(message = "destinationLat is required")
    Double destinationLat,

    @NotNull(message = "destinationLng is required")
    Double destinationLng

) {}
