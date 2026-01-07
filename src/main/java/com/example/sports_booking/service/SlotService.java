package com.example.sports_booking.service;

import com.example.sports_booking.dto.CreateSlotRequest;
import com.example.sports_booking.dto.SlotDTO;
import com.example.sports_booking.entity.Slot;
import com.example.sports_booking.entity.SlotStatus;
import com.example.sports_booking.entity.Venue;
import com.example.sports_booking.exception.ResourceNotFoundException;
import com.example.sports_booking.exception.SlotOverlapException;
import com.example.sports_booking.repository.SlotRepository;
import com.example.sports_booking.repository.VenueRepository;
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
public class SlotService {

    private final SlotRepository repo;
    private final VenueRepository venueRepo;

    @Transactional
    public SlotDTO addSlot(Long venueId, CreateSlotRequest request) {
        log.info("Adding slot for venue: {}, start: {}, end: {}", 
                venueId, request.getStartTime(), request.getEndTime());

        Venue venue = venueRepo.findById(venueId)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found with id: " + venueId));

        // Validate time range
        if (request.getStartTime().isAfter(request.getEndTime())) {
            throw new IllegalArgumentException("Start time must be before end time");
        }

        // Check for overlapping slots
        List<Slot> overlapping = repo.findOverlappingSlots(
                venueId,
                request.getStartTime(),
                request.getEndTime()
        );

        if (!overlapping.isEmpty()) {
            log.warn("Slot overlap detected for venue: {}", venueId);
            throw new SlotOverlapException(
                    "Slot overlaps with existing slots for venue: " + venue.getName()
            );
        }

        Slot slot = Slot.builder()
                .venue(venue)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(SlotStatus.AVAILABLE)
                .build();

        Slot saved = repo.save(slot);
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<SlotDTO> getByVenueId(Long venueId) {
        log.info("Fetching slots for venue: {}", venueId);
        return repo.findAll().stream()
                .filter(s -> s.getVenue().getId().equals(venueId))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SlotDTO> getAvailableSlots(Long venueId, LocalDateTime startTime, LocalDateTime endTime) {
        log.info("Fetching available slots for venue: {}, start: {}, end: {}", 
                venueId, startTime, endTime);
        return repo.findAvailableSlots(venueId, startTime, endTime, SlotStatus.AVAILABLE)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private SlotDTO toDTO(Slot slot) {
        return SlotDTO.builder()
                .id(slot.getId())
                .venueId(slot.getVenue().getId())
                .venueName(slot.getVenue().getName())
                .sportId(slot.getVenue().getSportId())
                .startTime(slot.getStartTime())
                .endTime(slot.getEndTime())
                .status(slot.getStatus().toString())
                .createdAt(slot.getCreatedAt())
                .updatedAt(slot.getUpdatedAt())
                .build();
    }
}
