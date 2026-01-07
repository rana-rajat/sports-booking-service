package com.example.sports_booking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateBookingRequest {
    @NotNull(message = "slotId is required")
    private Long slotId;

    @NotBlank(message = "userName is required")
    private String userName;
}
