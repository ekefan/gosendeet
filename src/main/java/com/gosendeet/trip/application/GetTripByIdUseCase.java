package com.gosendeet.trip.application;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.gosendeet.trip.application.dto.TripApiResponse;
import com.gosendeet.trip.application.exceptions.TripNotFoundException;
import com.gosendeet.trip.domain.TripRepository;

@Component
public class GetTripByIdUseCase {
    private final TripRepository tripRepo;

        public GetTripByIdUseCase(TripRepository tripRepository) {
        this.tripRepo = tripRepository;
    }

    public TripApiResponse execute(UUID tripId) {
        var tripOpt = tripRepo.findByTripId(tripId);

        if (tripOpt.isEmpty()) {
            throw new TripNotFoundException("Trip with ID " + tripId + " not found.");
        }

        var trip = tripOpt.get();

        return new TripApiResponse(
                trip.getId(),
                trip.getRiderId(),
                trip.getCustomerId(),
                trip.getPickupLat(),
                trip.getPickupLng(),
                trip.getDestinationLat(),
                trip.getDestinationLng(),
                trip.getStatus().name()
        );
    }
}
