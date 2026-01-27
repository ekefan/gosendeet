package com.gosendeet.trip.application;

import org.springframework.stereotype.Component;

import com.gosendeet.trip.application.exceptions.TripNotFoundException;
import com.gosendeet.trip.domain.TripRepository;

@Component
public class CancelTripUseCase {
        private final TripRepository tripRepo;

    public CancelTripUseCase(TripRepository tripRepository) {
        this.tripRepo = tripRepository;
    }

    public void execute(java.util.UUID tripId) {
        var trip = tripRepo.findByTripId(tripId).orElseThrow(
            () -> new TripNotFoundException(
                "Trip with ID " + tripId + " not found."
            )
        );
        trip.cancel();
        tripRepo.upsert(trip);
        return;
    }
}
