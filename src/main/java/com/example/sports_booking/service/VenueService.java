package com.example.sports_booking.service;

import com.example.sports_booking.dto.CreateVenueRequest;
import com.example.sports_booking.dto.VenueDTO;
import com.example.sports_booking.entity.Venue;
import com.example.sports_booking.exception.ResourceNotFoundException;
import com.example.sports_booking.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository repo;

    @Transactional
    public VenueDTO create(CreateVenueRequest request) {
        log.info("Creating venue: {}", request.getName());
        Venue venue = Venue.builder()
                .name(request.getName())
                .location(request.getLocation())
                .sportId(request.getSportId())
                .build();
        Venue saved = repo.save(venue);
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<VenueDTO> list() {
        log.info("Listing all venues");
        return repo.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public VenueDTO getById(Long id) {
        log.info("Fetching venue with id: {}", id);
        Venue venue = repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found with id: " + id));
        return toDTO(venue);
    }

    @Transactional(readOnly = true)
    public List<VenueDTO> getBySportId(String sportId) {
        log.info("Fetching venues for sport: {}", sportId);
        return repo.findBySportId(sportId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deleting venue with id: {}", id);
        if (!repo.existsById(id)) {
            throw new ResourceNotFoundException("Venue not found with id: " + id);
        }
        repo.deleteById(id);
    }

    private VenueDTO toDTO(Venue venue) {
        return VenueDTO.builder()
                .id(venue.getId())
                .name(venue.getName())
                .location(venue.getLocation())
                .sportId(venue.getSportId())
                .createdAt(venue.getCreatedAt())
                .updatedAt(venue.getUpdatedAt())
                .build();
    }
}
