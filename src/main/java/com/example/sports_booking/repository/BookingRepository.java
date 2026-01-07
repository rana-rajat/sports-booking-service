package com.example.sports_booking.repository;

import com.example.sports_booking.entity.Booking;
import com.example.sports_booking.entity.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserName(String userName);

    List<Booking> findByStatus(BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.slot.id = :slotId")
    List<Booking> findBySlotId(@Param("slotId") Long slotId);

    @Query("SELECT b FROM Booking b WHERE b.slot.venue.id = :venueId")
    List<Booking> findByVenueId(@Param("venueId") Long venueId);
}

