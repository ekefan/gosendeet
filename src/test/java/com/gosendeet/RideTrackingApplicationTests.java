package com.gosendeet;

import com.gosendeet.internal.db.DataStore;
import com.gosendeet.trip.domain.TripRepository;
import com.gosendeet.trip.infrastructure.InMemoryTripRepository;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@SpringBootTest
class RideTrackingApplicationTests {

    @Test
    void contextLoads() {
    }

    @Configuration
    static class TestConfig {
        @Bean
        public TripRepository tripRepository() {
            DataStore dataStore = new DataStore(); 
			TripRepository repo = new InMemoryTripRepository(dataStore);

            return repo;
        }
    }
}
