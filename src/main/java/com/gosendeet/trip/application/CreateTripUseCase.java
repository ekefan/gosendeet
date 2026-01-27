package com.gosendeet.trip.application;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.gosendeet.trip.application.dto.CreateTripRequest;
import com.gosendeet.trip.application.dto.TripApiResponse;
import com.gosendeet.trip.domain.Trip;
import com.gosendeet.trip.domain.TripRepository;

@Component
public class CreateTripUseCase {

    private final TripRepository tripRepo;

    public CreateTripUseCase(TripRepository tripRepository) {
        this.tripRepo = tripRepository;
    }

    public TripApiResponse execute(CreateTripRequest request) {
        UUID tripId = UUID.randomUUID();

        Trip trip = new Trip(
            tripId,

            request.customerId(),
            request.pickupLat(),
            request.pickupLng(),
            request.destinationLat(),
            request.destinationLng()
        );

        tripRepo.upsert(trip);

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
