package com.gosendeet.trip.application;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.gosendeet.trip.application.exceptions.TripNotFoundException;
import com.gosendeet.trip.domain.TripRepository;

@Component
public class MarkPackageAsDeliveredUseCase {
    private final TripRepository tripRepo;
    public MarkPackageAsDeliveredUseCase(TripRepository tripRepository) {
        this.tripRepo = tripRepository;
    }

    public void execute(UUID tripId) {
        var trip = tripRepo.findByTripId(tripId).orElseThrow(
            () -> new TripNotFoundException(
                "Trip with ID " + tripId + " not found."
            )
        );
        trip.markParcelDelivered();
        tripRepo.upsert(trip);
        return;
    }
}
