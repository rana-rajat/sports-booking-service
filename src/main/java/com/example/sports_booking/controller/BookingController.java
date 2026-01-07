package com.example.sports_booking.controller;

import com.example.sports_booking.dto.BookingDTO;
import com.example.sports_booking.dto.CreateBookingRequest;
import com.example.sports_booking.service.BookingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bookings")
public class BookingController {

    private final BookingService service;

    @PostMapping
    public ResponseEntity<BookingDTO> book(@Valid @RequestBody CreateBookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.book(request));
    }

    @GetMapping
    public ResponseEntity<List<BookingDTO>> listBookings() {
        return ResponseEntity.ok(service.listBookings());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookingDTO> getBooking(@PathVariable Long id) {
        return ResponseEntity.ok(service.getBooking(id));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        service.cancel(id);
        return ResponseEntity.noContent().build();
    }
}
