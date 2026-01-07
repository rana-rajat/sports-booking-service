package com.example.sports_booking.service;

import com.example.sports_booking.dto.AvailableVenueDTO;
import com.example.sports_booking.entity.Slot;
import com.example.sports_booking.entity.SlotStatus;
import com.example.sports_booking.entity.Venue;
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
public class AvailabilityService {

    private final SlotRepository slotRepository;
    private final VenueRepository venueRepository;

    @Transactional(readOnly = true)
    public List<AvailableVenueDTO> findAvailableVenues(String sportId, LocalDateTime startTime, LocalDateTime endTime) {
        log.info("Finding available venues - sportId: {}, startTime: {}, endTime: {}", sportId, startTime, endTime);

        List<Venue> venues;
        if (sportId != null && !sportId.isEmpty()) {
            venues = venueRepository.findBySportId(sportId);
        } else {
            venues = venueRepository.findAll();
        }

        return venues.stream()
                .flatMap(venue -> {
                    // Find available slots for this venue within the time range
                    List<Slot> availableSlots = slotRepository.findAvailableSlots(
                            venue.getId(),
                            startTime,
                            endTime,
                            SlotStatus.AVAILABLE
                    );

                    return availableSlots.stream()
                            .map(slot -> AvailableVenueDTO.builder()
                                    .venueId(venue.getId())
                                    .venueName(venue.getName())
                                    .location(venue.getLocation())
                                    .sportId(venue.getSportId())
                                    .slotId(slot.getId())
                                    .slotStartTime(slot.getStartTime())
                                    .slotEndTime(slot.getEndTime())
                                    .build());
                })
                .collect(Collectors.toList());
    }
}
