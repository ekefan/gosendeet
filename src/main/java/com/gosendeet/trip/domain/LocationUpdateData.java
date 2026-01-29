package com.gosendeet.trip.domain;

public class LocationUpdateData {
        public final double lat;
        public final double lng;
        public final double distanceMeters;
        public final long durationSeconds;

        public LocationUpdateData(double lat, double lng, double distanceMeters, long durationSeconds) {
            this.lat = lat;
            this.lng = lng;
            this.distanceMeters = distanceMeters;
            this.durationSeconds = durationSeconds;
        }
}
