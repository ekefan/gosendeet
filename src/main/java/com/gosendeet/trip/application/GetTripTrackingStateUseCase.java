package com.gosendeet.trip.application;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.gosendeet.trip.application.dto.TripTrackingStatusResponse;
import com.gosendeet.trip.application.exceptions.TripNotFoundException;
import com.gosendeet.trip.domain.TrackingState;
import com.gosendeet.trip.domain.TripRepository;

@Component
public class GetTripTrackingStateUseCase {
    private final TripRepository tripRepo;

    public GetTripTrackingStateUseCase(TripRepository tripRepository) {
        this.tripRepo = tripRepository;
    }
    public TripTrackingStatusResponse execute(UUID tripId) {
        var tripOpt = tripRepo.findByTripId(tripId);

        if (tripOpt.isEmpty()) {
            throw new TripNotFoundException("Trip with ID " + tripId + " not found.");
        }

        var trip = tripOpt.get();
        TrackingState tracking = trip.getTrackingState();

        return new TripTrackingStatusResponse(
            trip.getId(),
            new TripTrackingStatusResponse.Participants(
                    trip.getRiderId(),
                    trip.getCustomerId()
            ),
            new TripTrackingStatusResponse.TripStatus(
                    trip.getStatus().name()
            ),
            new TripTrackingStatusResponse.Tracking(
            tracking.getDistanceTravelledMeters(),
            tracking.getDistanceRemainingMeters(),
            tracking.getTimeElaspsedSeconds().toSeconds(),
            tracking.getEtaSeconds().toSeconds(),
            tracking.getIsMoving()
            ),
            trip.getUpdatedAt()
        );

    }
}
