package com.gosendeet.trip.application;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.gosendeet.trip.application.dto.RideLocationUpdate;
import com.gosendeet.trip.domain.TripRepository;
import com.gosendeet.trip.domain.LocationUpdateData;

@Component
public class AcceptRiderLocationUseCase {
    private final TripRepository tripRepo;
    public AcceptRiderLocationUseCase(TripRepository tripRepository) {
        this.tripRepo = tripRepository;
    }

    public void execute(UUID tripId, RideLocationUpdate updateData) {
        var trip = tripRepo.findByTripId(tripId).orElseThrow(
            () -> new com.gosendeet.trip.application.exceptions.TripNotFoundException(
                "Trip with ID " + tripId + " not found."
            )
        );
        LocationUpdateData update = new LocationUpdateData(
            updateData.lat(),
            updateData.lng(),
            updateData.distance().value(),
            (long)updateData.duration().value()
        );

        trip.processLocationUpdate(update);
        tripRepo.upsert(trip);
        return;
    }
}
