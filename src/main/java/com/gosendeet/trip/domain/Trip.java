package com.gosendeet.trip.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class Trip {
    private UUID id;

    private UUID riderId;
    private UUID customerId;
    private String region;
    private double startLat;
    private double startLng;
    private String startAddress;

    private double endLat;
    private double endLng;
    private String endAddress;

    private double plannedDistanceMeters;
    private Duration plannedEta;

    private double actualDistanceMeters;
    private Duration actualDuration;
    private TripStatus status;

    private Instant createdAt;
    private Instant startedAt;
    private Instant completedAt;
    private Instant updatedAt;
    private TrackingState trackingState;
    private String travelMode;
    private Boolean parcelDeliveredSuccessfully;

    public Trip(
            UUID id, UUID customerId,
            double startLat, double startLng,
            String startAddress, double endLat,
            double endLng, String endAddress,
            double plannedDistanceMeters, Duration plannedEta,
            String travelMode, String region
        ) {
        this.trackingState = TrackingState.init(
            plannedDistanceMeters,
            plannedEta,
            Instant.now()
        );

        this.travelMode = travelMode;
        this.id = id;
        this.customerId = customerId;
        this.region = region;
        this.startLat = startLat;
        this.startLng = startLng;
        this.startAddress = startAddress;

        this.endLat = endLat;
        this.endLng = endLng;
        this.endAddress = endAddress;

        this.plannedDistanceMeters = plannedDistanceMeters;
        this.plannedEta = plannedEta;

        this.actualDistanceMeters = 0;
        this.status = TripStatus.CREATED;

        this.createdAt = Instant.now();
        this.updatedAt = this.createdAt;
        this.parcelDeliveredSuccessfully = false;
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
        ensureNotCompleted();
        if (this.status != TripStatus.ASSIGNED) {
            throw new IllegalStateException("Trip must be ASSIGNED to start");
        }
        this.status = TripStatus.IN_PROGRESS;
        this.startedAt = Instant.now();
        this.updatedAt = this.startedAt;
    }

    public void markParcelDelivered() {
        ensureNotCompleted();
        this.parcelDeliveredSuccessfully = true;
        this.updatedAt = Instant.now();
    }

    public void complete() {
        ensureInProgress();

        if (!this.parcelDeliveredSuccessfully) {
            throw new IllegalStateException(
                "Cannot complete trip: parcel not delivered successfully"
            );
        }
        
        this.status = TripStatus.COMPLETED;
        this.completedAt = Instant.now();
        this.actualDuration = Duration.between(startedAt, completedAt);
        this.updatedAt = Instant.now();
    }

    public void cancel() {
        ensureNotCompleted();
        this.status = TripStatus.CANCELLED;
        this.updatedAt = Instant.now();
    }

    private void ensureNotCompleted() {
        if ((this.status == TripStatus.COMPLETED) || 
            (this.status == TripStatus.CANCELLED)) {
            throw new IllegalStateException(
                "Trip is already completed or cancelled and cannot be modified."
            );
        }
    }

    private void ensureInProgress() {
        if (this.status != TripStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                "Trip must be IN_PROGRESS to perform this action."
            );
        }
    }


    public void processLocationUpdate(LocationUpdateData updateData){
        ensureNotCompleted();
        ensureInProgress();
        TrackingState tracking = this.getTrackingState();
        
        Instant now = Instant.now();

        double distanceRemaining = updateData.distanceMeters;
        double distanceTraveled =  Math.max(this.getPlannedDistanceMeters() - distanceRemaining, 0);

        Duration durationTravelled = Duration.between(this.getStartedAt(), now);

        Duration eta = Duration.ofSeconds(updateData.durationSeconds);

        boolean isMoving = true;
        if (distanceTraveled == tracking.getDistanceTravelledMeters()) {
            isMoving = false;
        }

        tracking.setDistanceTravelledMeters(distanceTraveled);
        tracking.setDistanceRemainingMeters(distanceRemaining);
        tracking.setTimeElaspsedSeconds(durationTravelled);
        tracking.setEtaSeconds(eta);
        tracking.setLastLocationUpdateAt(now);
        tracking.setIsMoving(isMoving);
        tracking.setLastLat(updateData.lat);
        tracking.setLastLng(updateData.lng);

        this.setActualDistanceMeters(distanceTraveled);
        this.setActualDuration(durationTravelled);
        this.setUpdatedAt(now);
    }

}
