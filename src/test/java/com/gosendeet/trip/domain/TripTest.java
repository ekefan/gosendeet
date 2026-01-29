package com.gosendeet.trip.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

public class TripTest {
    
    private static final double DELTA = 0.001;
    
    @Nested
    @DisplayName("Trip Creation Tests")
    class TripCreationTests {
        @Test
        @DisplayName("Should create trip with correct initial state")
        void createTrip_ShouldHaveCorrectInitialState() {
            Trip trip = createTestTrip();
            assertEquals(TripStatus.CREATED, trip.getStatus());
            assertEquals(0, trip.getActualDistanceMeters(), DELTA);
            assertNull(trip.getRiderId());
            assertNull(trip.getStartedAt());
            assertNull(trip.getCompletedAt());
            assertNotNull(trip.getCreatedAt());
            assertNotNull(trip.getUpdatedAt());
            assertFalse(trip.getParcelDeliveredSuccessfully());
            
            assertNotNull(trip.getTrackingState());
            assertEquals(0, trip.getTrackingState().getDistanceTravelledMeters(), DELTA);
            assertEquals(3452.3, trip.getTrackingState().getDistanceRemainingMeters(), DELTA);
            assertFalse(trip.getTrackingState().getIsMoving());
        }
    }
        
    @Nested
    @DisplayName("Location Update Tests")
    class LocationUpdateTests {
        
        @Test
        @DisplayName("Should process location update and update tracking state")
        void processLocationUpdate_ShouldUpdateTrackingState() {
            Trip trip = createTestTrip();
            trip.assignRider(UUID.randomUUID());
            trip.start();
            
            double remainingDistance = 2500.0; // 2.5km remaining out of 3.45km
            long etaSeconds = 1200; // 20 minutes
            LocationUpdateData updateData = new LocationUpdateData(
                10.5000, 
                30.6000, 
                remainingDistance, 
                etaSeconds
            );
            
            trip.processLocationUpdate(updateData);
            
            TrackingState tracking = trip.getTrackingState();
            
            // Should have traveled approximately 952.3 meters (3452.3 - 2500) of road distance
            assertEquals(952.3, tracking.getDistanceTravelledMeters(), DELTA);
            assertEquals(remainingDistance, tracking.getDistanceRemainingMeters(), DELTA);
            assertEquals(Duration.ofSeconds(etaSeconds), tracking.getEtaSeconds());
            assertEquals(10.5000, tracking.getLastLat(), DELTA);
            assertEquals(30.6000, tracking.getLastLng(), DELTA);
            assertTrue(tracking.getIsMoving());
            assertNotNull(tracking.getLastLocationUpdateAt());
            
            // Trip level fields should also be updated
            assertEquals(952.3, trip.getActualDistanceMeters(), DELTA);
            assertNotNull(trip.getActualDuration());
        }
        
        @Test
        @DisplayName("Should detect stationary state when distance doesn't change")
        void processLocationUpdate_WhenStationary_ShouldMarkAsNotMoving() {
            Trip trip = createTestTrip();
            trip.assignRider(UUID.randomUUID());
            trip.start();
            
            LocationUpdateData update1 = new LocationUpdateData(
                10.5000, 30.6000, 2500.0, 1200
            );
            trip.processLocationUpdate(update1);
            
            LocationUpdateData update2 = new LocationUpdateData(
                10.5001, 30.6001, 2500.0, 1180
            );
            
            trip.processLocationUpdate(update2);
            
            assertFalse(trip.getTrackingState().getIsMoving());
        }
        
        @Test
        @DisplayName("Should update ETA as trip progresses")
        void processLocationUpdate_ShouldUpdateETA() {
            Trip trip = createTestTrip();
            trip.assignRider(UUID.randomUUID());
            trip.start();
            LocationUpdateData update1 = new LocationUpdateData(
                20.0, 40.0, 1726.15, 1600
            );
            trip.processLocationUpdate(update1);
            
            Duration firstEta = trip.getTrackingState().getEtaSeconds();
            
            LocationUpdateData update2 = new LocationUpdateData(
                25.0, 45.0, 863.075, 800
            );
            trip.processLocationUpdate(update2);
            
            Duration secondEta = trip.getTrackingState().getEtaSeconds();
            assertTrue(secondEta.getSeconds() < firstEta.getSeconds());
        }
        
        @Test
        @DisplayName("Should accumulate distance traveled over multiple updates")
        void processLocationUpdate_MultipleUpdates_ShouldAccumulateDistance() {
            Trip trip = createTestTrip();
            trip.assignRider(UUID.randomUUID());
            trip.start();
            
            // Update 1: 1/4 of trip
            trip.processLocationUpdate(new LocationUpdateData(
                15.0, 35.0, 2589.225, 2400
            ));
            
            // Update 2: 1/2 of trip
            trip.processLocationUpdate(new LocationUpdateData(
                20.0, 40.0, 1726.15, 1600
            ));
            
            // Update 3: 3/4 of trip
            trip.processLocationUpdate(new LocationUpdateData(
                30.0, 45.0, 863.075, 800
            ));
            
            
            TrackingState tracking = trip.getTrackingState();
            assertEquals(2589.225, tracking.getDistanceTravelledMeters(), DELTA);
            assertEquals(863.075, tracking.getDistanceRemainingMeters(), DELTA);
        }
        
        @Test
        @DisplayName("Should fail to process location update when trip not in progress")
        void processLocationUpdate_WhenNotInProgress_ShouldThrowException() {
            Trip trip = createTestTrip();
            LocationUpdateData updateData = new LocationUpdateData(
                10.5, 30.6, 2500.0, 1200
            );
            assertThrows(IllegalStateException.class, () -> {
                trip.processLocationUpdate(updateData);
            });
        }
        
        @Test
        @DisplayName("Should fail to process location update when trip is completed")
        void processLocationUpdate_WhenCompleted_ShouldThrowException() {
            Trip trip = createAndCompleteTrip();
            LocationUpdateData updateData = new LocationUpdateData(
                40.3255, 52.2134, 0.0, 0
            );
            assertThrows(IllegalStateException.class, () -> {
                trip.processLocationUpdate(updateData);
            });
        }
        
        @Test
        @DisplayName("Should handle location update with zero remaining distance")
        void processLocationUpdate_WithZeroRemainingDistance_ShouldWork() {
            Trip trip = createTestTrip();
            trip.assignRider(UUID.randomUUID());
            trip.start();
            
            LocationUpdateData updateData = new LocationUpdateData(
                40.3255, 52.2134, 0.0, 0
            );
            
            trip.processLocationUpdate(updateData);
            TrackingState tracking = trip.getTrackingState();
            assertEquals(3452.3, tracking.getDistanceTravelledMeters(), DELTA);
            assertEquals(0.0, tracking.getDistanceRemainingMeters(), DELTA);
            assertEquals(Duration.ZERO, tracking.getEtaSeconds());
        }
    }
    
    @Nested
    @DisplayName("Trip Completion Tests")
    class TripCompletionTests {
        
        @Test
        @DisplayName("Should complete trip when parcel is delivered")
        void completeTrip_WhenParcelDelivered_ShouldSucceed() {
            Trip trip = createTestTrip();
            trip.assignRider(UUID.randomUUID());
            trip.start();
            trip.markParcelDelivered();
            trip.complete();
            assertEquals(TripStatus.COMPLETED, trip.getStatus());
            assertNotNull(trip.getCompletedAt());
            assertNotNull(trip.getActualDuration());
            assertTrue(trip.getActualDuration().getSeconds() >= 0);
        }
        
        @Test
        @DisplayName("Should fail to complete trip when parcel not delivered")
        void completeTrip_WhenParcelNotDelivered_ShouldThrowException() {
            Trip trip = createTestTrip();
            trip.assignRider(UUID.randomUUID());
            trip.start();
            
            IllegalStateException exception = assertThrows(
                IllegalStateException.class, 
                () -> trip.complete()
            );
            assertTrue(exception.getMessage().contains("parcel not delivered"));
        }
        
        @Test
        @DisplayName("Should fail to complete trip when not in progress")
        void completeTrip_WhenNotInProgress_ShouldThrowException() {
            Trip trip = createTestTrip();
            trip.assignRider(UUID.randomUUID());
            assertThrows(IllegalStateException.class, () -> {
                trip.complete();
            });
        }
    }
    
    private Trip createTestTrip() {
        return new Trip(
            UUID.randomUUID(),
            UUID.randomUUID(),
            10.2356,
            30.4334,
            "World Trade Center",
            40.3255,
            52.2134,
            "221B Baker Street",
            3_452.3,
            Duration.ofSeconds(3_200),
            "driving",
            "abj"
        );
    }
    
    private Trip createAndCompleteTrip() {
        Trip trip = createTestTrip();
        trip.assignRider(UUID.randomUUID());
        trip.start();
        trip.markParcelDelivered();
        trip.complete();
        return trip;
    }
}