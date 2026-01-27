package com.gosendeet.trip.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Getter
@NoArgsConstructor
public class Trip {
    private UUID id;

    private UUID riderId;
    private UUID customerId;

    private double pickupLat;
    private double pickupLng;

    private double destinationLat;
    private double destinationLng;
    
    private TripStatus status;

    private Instant createdAt;
    private Instant updatedAt;

    public Trip(UUID id,
                UUID customerId,
                double pickupLat,
                double pickupLng,
                double destinationLat,
                double destinationLng) {

        this.id = id;
        this.customerId = customerId;
        this.pickupLat = pickupLat;
        this.pickupLng = pickupLng;
        this.destinationLat = destinationLat;
        this.destinationLng = destinationLng;
        this.status = TripStatus.CREATED;
        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
    }
    public void assignRider(UUID riderId) {
        ensureNotCompleted();
        if (this.status != TripStatus.CREATED){
            throw new IllegalStateException(
                "Cannot assign a trip that has not been created"
            );
        }
        this.riderId = riderId;
        this.status = TripStatus.ASSIGNED;
        this.updatedAt = Instant.now();
    }

    public void start() {
        assert  this.riderId != null;
        ensureNotCompleted();
        if (this.status != TripStatus.ASSIGNED) {
            throw new IllegalStateException(
                "Cannot start trip in current state: " + status
            );
        }
        this.status = TripStatus.IN_PROGRESS;
        this.updatedAt = Instant.now();
    }

    public void complete() {
        if (this.status != TripStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                "Cannot complete trip in current state: " + status
            );
        }
        this.status = TripStatus.COMPLETED;
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        ensureNotCompleted();
        if (this.status == TripStatus.CANCELLED) {
            throw new IllegalStateException("Trip is already cancelled");
        }
        this.status = TripStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    private void ensureNotCompleted() {
        if (this.status == TripStatus.COMPLETED) {
            throw new IllegalStateException(
                "Cannot perform this action on a completed trip"
            );
        }
    }
}
