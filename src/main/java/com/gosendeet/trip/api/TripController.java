package com.gosendeet.trip.api;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gosendeet.trip.application.CreateTripUseCase;
import com.gosendeet.trip.application.GetTripByIdUseCase;
import com.gosendeet.trip.application.dto.CreateTripRequest;
import com.gosendeet.trip.application.dto.TripApiResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/trip")
public class TripController {

    private final CreateTripUseCase createTripUseCase;
    private final GetTripByIdUseCase getTripByIdUseCase;

    public TripController(
        CreateTripUseCase createTripUseCase, 
        GetTripByIdUseCase getTripByIdUseCase
    ){
        this.createTripUseCase = createTripUseCase;
        this.getTripByIdUseCase = getTripByIdUseCase;
    }

    @PostMapping
    public ResponseEntity<TripApiResponse> createTrip(@RequestBody @Valid CreateTripRequest req) {
        TripApiResponse response = createTripUseCase.execute(req);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{tripId}")
                .buildAndExpand(response.tripId())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    @GetMapping("/{tripId}")
    public ResponseEntity<TripApiResponse> getTrip(@PathVariable UUID tripId) {
        TripApiResponse response = getTripByIdUseCase.execute(tripId);
        return ResponseEntity.ok(response);
    }
}
