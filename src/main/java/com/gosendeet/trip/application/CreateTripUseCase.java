package com.gosendeet.trip.application;

import java.time.Duration;
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
            request.startLat(),
            request.startLng(),
            request.startAddress(),
            request.endLat(),
            request.endLng(),
            request.endAddress(),
            request.plannedDistanceMeters(),
            Duration.ofSeconds((long) request.plannedEtaSeconds()),
            request.travelMode(),
            request.region()
        );

        tripRepo.upsert(trip);

        return new TripApiResponse(
                trip.getId(),
                trip.getRiderId(),
                trip.getCustomerId(),
                trip.getStartLat(),
                trip.getStartLng(),
                trip.getEndLat(),
                trip.getEndLng(),
                trip.getStatus().name(),
                trip.getRegion(),
                trip.getTravelMode(),
                trip.getPlannedDistanceMeters(),
                trip.getPlannedEta().toSeconds(),
                trip.getStartAddress(),
                trip.getEndAddress()
        );
    }
}
