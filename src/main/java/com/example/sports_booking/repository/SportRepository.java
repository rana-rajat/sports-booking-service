package com.example.sports_booking.repository;

import com.example.sports_booking.entity.Sport;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SportRepository extends JpaRepository<Sport, Long> {
    Optional<Sport> findBySportId(String sportId);
}
