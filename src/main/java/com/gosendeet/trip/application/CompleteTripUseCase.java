package com.gosendeet.trip.application;

import org.springframework.stereotype.Component;

import com.gosendeet.trip.application.exceptions.TripNotFoundException;
import com.gosendeet.trip.domain.TripRepository;

@Component
public class CompleteTripUseCase {
    private final TripRepository tripRepo;
    public CompleteTripUseCase(TripRepository tripRepository) {
        this.tripRepo = tripRepository;
    }
    public void execute(java.util.UUID tripId) {
        var trip = tripRepo.findByTripId(tripId).orElseThrow(
            () -> new TripNotFoundException(
                "Trip with ID " + tripId + " not found."
            )
        );
        trip.complete();
        tripRepo.upsert(trip);
        return;
    }
}
