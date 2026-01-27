package com.gosendeet.internal.db;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;

import com.gosendeet.trip.domain.Trip;

@Component
public class DataStore {

    private final Map<UUID, Trip> store = new ConcurrentHashMap<>();

    public void putTrip(Trip trip) {
        store.put(trip.getId(), trip);
    }

    public Optional<Trip> getTripById(UUID tripId) {
        return Optional.ofNullable(store.get(tripId));
    }

    public void deleteTrip(UUID tripId) {
        store.remove(tripId);
    }

    public boolean exists(UUID tripId) {
        return store.containsKey(tripId);
    }
}
