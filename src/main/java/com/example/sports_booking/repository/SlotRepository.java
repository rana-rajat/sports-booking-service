package com.example.sports_booking.repository;

import com.example.sports_booking.entity.Slot;
import com.example.sports_booking.entity.SlotStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.List;

public interface SlotRepository extends JpaRepository<Slot, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM Slot s WHERE s.id = :id")
    Slot lockById(Long id);

    @Query("SELECT s FROM Slot s WHERE s.venue.id = :venueId AND s.status = :status AND " +
           "s.startTime >= :startTime AND s.endTime <= :endTime")
    List<Slot> findAvailableSlots(@Param("venueId") Long venueId,
                                   @Param("startTime") LocalDateTime startTime,
                                   @Param("endTime") LocalDateTime endTime,
                                   @Param("status") SlotStatus status);

    @Query("SELECT s FROM Slot s WHERE s.venue.id = :venueId AND " +
           "((s.startTime < :endTime AND s.endTime > :startTime))")
    List<Slot> findOverlappingSlots(@Param("venueId") Long venueId,
                                     @Param("startTime") LocalDateTime startTime,
                                     @Param("endTime") LocalDateTime endTime);

    @Query("SELECT s FROM Slot s WHERE s.venue.sportId = :sportId AND s.status = :status AND " +
           "s.startTime >= :startTime AND s.endTime <= :endTime")
    List<Slot> findAvailableSlotsBySport(@Param("sportId") String sportId,
                                         @Param("startTime") LocalDateTime startTime,
                                         @Param("endTime") LocalDateTime endTime,
                                         @Param("status") SlotStatus status);
}
