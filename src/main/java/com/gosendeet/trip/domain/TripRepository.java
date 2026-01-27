package com.gosendeet.trip.domain;

import java.util.Optional;
import java.util.UUID;

public interface TripRepository {

    void upsert(Trip trip);

    Optional<Trip> findByTripId(UUID tripId);
}
