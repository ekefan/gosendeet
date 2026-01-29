package com.gosendeet.trip.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RideLocationUpdate(

    @NotNull(message = "distance is required")
    Distance distance,

    @NotNull(message = "duration is required")
    Duration duration,

    @NotNull(message = "lat is required")
    Double lat,

    @NotNull(message = "lng is required")
    Double lng
) {

    public record Distance(
        @NotBlank(message = "distance text is required")
        String text,

        @NotNull(message = "distance value is required")
        Double value
    ) {}

    public record Duration(
        @NotBlank(message = "duration text is required")
        String text,

        @NotNull(message = "duration value is required")
        Long value
    ) {}
}
