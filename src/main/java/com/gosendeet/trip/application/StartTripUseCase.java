package com.gosendeet.trip.application;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.gosendeet.trip.application.exceptions.TripNotFoundException;
import com.gosendeet.trip.domain.TripRepository;

@Component
public class StartTripUseCase {

    private final TripRepository tripRepo;

    public StartTripUseCase(TripRepository tripRepository) {
        this.tripRepo = tripRepository;
    }

    public void execute(UUID tripId) {
        var trip = tripRepo.findByTripId(tripId).orElseThrow(
            () -> new TripNotFoundException(
                "Trip with ID " + tripId + " not found."
            )
        );
        trip.start();
        tripRepo.upsert(trip);
        return;
    }
}

