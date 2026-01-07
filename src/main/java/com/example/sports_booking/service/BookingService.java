package com.example.sports_booking.service;

import com.example.sports_booking.dto.BookingDTO;
import com.example.sports_booking.dto.CreateBookingRequest;
import com.example.sports_booking.entity.Booking;
import com.example.sports_booking.entity.BookingStatus;
import com.example.sports_booking.entity.Slot;
import com.example.sports_booking.entity.SlotStatus;
import com.example.sports_booking.exception.BookingException;
import com.example.sports_booking.exception.ResourceNotFoundException;
import com.example.sports_booking.repository.BookingRepository;
import com.example.sports_booking.repository.SlotRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingService {

    private final SlotRepository slotRepo;
    private final BookingRepository bookingRepo;

    @Transactional
    public BookingDTO book(CreateBookingRequest request) {
        log.info("Booking slot: {} for user: {}", request.getSlotId(), request.getUserName());

        Slot slot = slotRepo.lockById(request.getSlotId());

        if (slot == null) {
            throw new ResourceNotFoundException("Slot not found with id: " + request.getSlotId());
        }

        if (slot.getStatus() == SlotStatus.BOOKED) {
            log.warn("Double booking attempt for slot: {}", request.getSlotId());
            throw new BookingException("Slot is already booked");
        }

        slot.setStatus(SlotStatus.BOOKED);
        slotRepo.save(slot);

        Booking booking = Booking.builder()
                .slot(slot)
                .userName(request.getUserName())
                .status(BookingStatus.CONFIRMED)
                .build();

        Booking saved = bookingRepo.save(booking);
        log.info("Booking confirmed with id: {}", saved.getId());
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<BookingDTO> listBookings() {
        log.info("Fetching all bookings");
        return bookingRepo.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BookingDTO getBooking(Long bookingId) {
        log.info("Fetching booking with id: {}", bookingId);
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
        return toDTO(booking);
    }

    @Transactional
    public void cancel(Long bookingId) {
        log.info("Cancelling booking: {}", bookingId);
        Booking booking = bookingRepo.findById(bookingId)
                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BookingException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setCancelledAt(LocalDateTime.now());
        booking.getSlot().setStatus(SlotStatus.AVAILABLE);

        bookingRepo.save(booking);
        slotRepo.save(booking.getSlot());
        log.info("Booking cancelled: {}", bookingId);
    }

    private BookingDTO toDTO(Booking booking) {
        return BookingDTO.builder()
                .id(booking.getId())
                .slotId(booking.getSlot().getId())
                .venueId(booking.getSlot().getVenue().getId())
                .venueName(booking.getSlot().getVenue().getName())
                .userName(booking.getUserName())
                .slotStartTime(booking.getSlot().getStartTime())
                .slotEndTime(booking.getSlot().getEndTime())
                .status(booking.getStatus().toString())
                .createdAt(booking.getCreatedAt())
                .cancelledAt(booking.getCancelledAt())
                .build();
    }
}
