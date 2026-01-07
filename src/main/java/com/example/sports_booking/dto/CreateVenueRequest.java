package com.example.sports_booking.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateVenueRequest {
    @NotBlank(message = "name is required")
    private String name;

    @NotBlank(message = "location is required")
    private String location;

    @NotBlank(message = "sportId is required")
    private String sportId;
}
