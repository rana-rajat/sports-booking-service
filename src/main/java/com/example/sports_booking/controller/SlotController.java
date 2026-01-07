package com.example.sports_booking.controller;

import com.example.sports_booking.dto.CreateSlotRequest;
import com.example.sports_booking.dto.SlotDTO;
import com.example.sports_booking.dto.AvailableVenueDTO;
import com.example.sports_booking.service.SlotService;
import com.example.sports_booking.service.AvailabilityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SlotController {

    private final SlotService slotService;
    private final AvailabilityService availabilityService;

    @PostMapping("/venues/{venueId}/slots")
    public ResponseEntity<SlotDTO> addSlot(
            @PathVariable Long venueId,
            @Valid @RequestBody CreateSlotRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(slotService.addSlot(venueId, request));
    }

    @GetMapping("/venues/available")
    public ResponseEntity<List<AvailableVenueDTO>> getAvailableVenues(
            @RequestParam(required = false) String sportId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime startTime,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime endTime) {
        return ResponseEntity.ok(availabilityService.findAvailableVenues(sportId, startTime, endTime));
    }
}
