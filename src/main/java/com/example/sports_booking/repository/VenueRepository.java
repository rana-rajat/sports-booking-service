package com.example.sports_booking.repository;

import com.example.sports_booking.entity.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VenueRepository extends JpaRepository<Venue, Long> {
    List<Venue> findBySportId(String sportId);

    @Query("SELECT DISTINCT v FROM Venue v WHERE v.sportId = :sportId")
    List<Venue> findAllBySportId(@Param("sportId") String sportId);
}
