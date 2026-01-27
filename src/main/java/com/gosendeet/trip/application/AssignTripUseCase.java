package com.gosendeet.trip.application;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.gosendeet.trip.domain.TripRepository;

@Component
public class AssignTripUseCase {
    private final TripRepository tripRepo;
    public AssignTripUseCase(TripRepository tripRepository) {
        this.tripRepo = tripRepository;
    }
    public void execute(UUID tripId, UUID riderId) {
        var trip = tripRepo.findByTripId(tripId).orElseThrow(
            () -> new com.gosendeet.trip.application.exceptions.TripNotFoundException(
                "Trip with ID " + tripId + " not found."
            )
        );
        trip.assignRider(riderId);
        tripRepo.upsert(trip);
        return;
    }
}
