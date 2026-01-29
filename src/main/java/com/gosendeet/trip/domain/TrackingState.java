package com.gosendeet.trip.domain;

import java.time.Duration;
import java.time.Instant;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TrackingState {
    private double distanceTravelledMeters;
    private double distanceRemainingMeters;
    private Duration timeElaspsedSeconds;
    private Duration etaSeconds;
    private Instant lastLocationUpdateAt;
    private boolean isMoving;
    private double lastLat;
    private double lastLng;

    public void setIsMoving(boolean value) {
        isMoving = value;
    }

    public boolean getIsMoving() {
        return isMoving;
    }

    public static TrackingState init(double distanceRemainingMeters, Duration etaSeconds, Instant lastUpdate) {
        TrackingState state = new TrackingState();
        state.distanceTravelledMeters = 0;
        state.distanceRemainingMeters = distanceRemainingMeters;
        state.timeElaspsedSeconds = Duration.ZERO;
        state.etaSeconds = etaSeconds;
        state.lastLocationUpdateAt = lastUpdate;
        state.isMoving = false;
        return state;
    }
}