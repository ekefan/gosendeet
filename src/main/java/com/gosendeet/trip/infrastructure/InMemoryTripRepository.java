package com.gosendeet.trip.infrastructure;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Repository;

import com.gosendeet.internal.db.DataStore;
import com.gosendeet.trip.domain.Trip;
import com.gosendeet.trip.domain.TripRepository;

@Repository
public class InMemoryTripRepository implements TripRepository {

    private final DataStore dataStore;

    public InMemoryTripRepository(DataStore dataStore) {
        this.dataStore = dataStore;
    }

    @Override
    public void upsert(Trip trip) {
        dataStore.putTrip(trip);
    }

    @Override
    public Optional<Trip> findByTripId(UUID tripId) {
        return dataStore.getTripById(tripId);
    }
}