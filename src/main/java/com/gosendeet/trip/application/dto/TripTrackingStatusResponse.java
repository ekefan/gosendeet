package com.gosendeet.trip.application.dto;

import java.time.Instant;
import java.util.UUID;

public record TripTrackingStatusResponse(
        UUID tripId,
        Participants participants,
        TripStatus status,
        Tracking tracking,
        Instant lastUpdatedAt
) {

    public record Participants(
            UUID riderId,
            UUID customerId
    ) {}

    public record TripStatus(
            String value
    ) {}

    public record Tracking(
            double distanceTravelledMeters,
            double distanceRemainingMeters,
            long timeElaspedSeconds,
            long etaSeconds,
            boolean moving
    ) {}
}
