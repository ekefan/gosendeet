package com.gosendeet.trip.api;

import java.net.URI;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.gosendeet.trip.application.AcceptRiderLocationUseCase;
import com.gosendeet.trip.application.AssignTripUseCase;
import com.gosendeet.trip.application.CancelTripUseCase;
import com.gosendeet.trip.application.CompleteTripUseCase;
import com.gosendeet.trip.application.CreateTripUseCase;
import com.gosendeet.trip.application.GetTripByIdUseCase;
import com.gosendeet.trip.application.GetTripTrackingStateUseCase;
import com.gosendeet.trip.application.MarkPackageAsDeliveredUseCase;
import com.gosendeet.trip.application.StartTripUseCase;
import com.gosendeet.trip.application.dto.CreateTripRequest;
import com.gosendeet.trip.application.dto.RideLocationUpdate;
import com.gosendeet.trip.application.dto.TripApiResponse;
import com.gosendeet.trip.application.dto.TripTrackingStatusResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/trip")
public class TripController {

    private final CreateTripUseCase createTripUseCase;
    private final GetTripByIdUseCase getTripByIdUseCase;
    private final GetTripTrackingStateUseCase getTripTrackingStateUseCase;
    private final AcceptRiderLocationUseCase acceptRiderLocationUseCase;

    private final CancelTripUseCase cancelTripUseCase;
    private final CompleteTripUseCase completeTripUseCase;
    private final StartTripUseCase startTripUseCase;
    private final AssignTripUseCase assignTripUseCase;
    private final MarkPackageAsDeliveredUseCase markPackageAsDeliveredUseCase;

    public TripController(
        CreateTripUseCase createTripUseCase, 
        GetTripByIdUseCase getTripByIdUseCase,
        GetTripTrackingStateUseCase getTripTrackingStateUseCase,
        AcceptRiderLocationUseCase acceptRiderLocationUseCase,
        CancelTripUseCase cancelTripUseCase,
        CompleteTripUseCase completeTripUseCase,
        StartTripUseCase startTripUseCase,
        AssignTripUseCase assignTripUseCase,
        MarkPackageAsDeliveredUseCase markPackageAsDeliveredUseCase
    ){
        this.createTripUseCase = createTripUseCase;
        this.getTripByIdUseCase = getTripByIdUseCase;
        this.getTripTrackingStateUseCase = getTripTrackingStateUseCase;
        this.acceptRiderLocationUseCase = acceptRiderLocationUseCase;
        this.cancelTripUseCase = cancelTripUseCase;
        this.completeTripUseCase = completeTripUseCase;
        this.startTripUseCase = startTripUseCase;
        this.assignTripUseCase = assignTripUseCase;
        this.markPackageAsDeliveredUseCase = markPackageAsDeliveredUseCase;
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
    public ResponseEntity<TripApiResponse> getTrip(@PathVariable @Valid UUID tripId) {
        TripApiResponse response = getTripByIdUseCase.execute(tripId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{tripId}/parcel")
    public ResponseEntity<Void> markParcelDelivered(@PathVariable @Valid UUID tripId) {
        // Here we assume that marking the parcel as delivered is always successful.
        // In an actual scenario, we would pass additional information.
        markPackageAsDeliveredUseCase.execute(tripId);
        return ResponseEntity.noContent().build();
    }
    
    @PatchMapping("/{tripId}/assign/{riderId}")
    public ResponseEntity<Void> assignTrip(@PathVariable @Valid UUID tripId, @PathVariable @Valid UUID riderId) {
        assignTripUseCase.execute(tripId, riderId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{tripId}/start")
    public ResponseEntity<Void> startTrip(@PathVariable @Valid UUID tripId) {
        startTripUseCase.execute(tripId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{tripId}/cancel")
    public ResponseEntity<Void> cancelTrip(@PathVariable @Valid UUID tripId) {
        cancelTripUseCase.execute(tripId);
        return ResponseEntity.noContent().build();
    }
    @PatchMapping("/{tripId}/complete")
    public ResponseEntity<Void> completeTrip(@PathVariable @Valid UUID tripId) {
        completeTripUseCase.execute(tripId);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{tripId}/tracking")
    public ResponseEntity<TripTrackingStatusResponse> getTrackingStatus(@PathVariable @Valid UUID tripId) {
        TripTrackingStatusResponse response = getTripTrackingStateUseCase.execute(tripId);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{tripId}/tracking")
    public ResponseEntity<Void> updateRiderLocation(
        @PathVariable @Valid UUID tripId,
        @RequestBody @Valid RideLocationUpdate updateReq) {
        acceptRiderLocationUseCase.execute(tripId, updateReq);
            return ResponseEntity.noContent().build();
        }
}
