package com.example.Room.Booking.System.repos;

import com.example.Room.Booking.System.model.Bookings;
import com.example.Room.Booking.System.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepo extends JpaRepository<Bookings, Integer>{
    @Query("SELECT b FROM Bookings b WHERE b.room.roomId = :roomId AND b.dateOfBooking = :dateOfBooking")
    List<Bookings> getAllBookingsOn(@Param("roomId") Integer roomId, @Param("dateOfBooking") LocalDate dateOfBooking);

    @Query("SELECT b FROM Bookings b WHERE b.user.userId = :userId")
    List<Bookings> findByUser(Integer userId);

    @Query("SELECT b FROM Bookings b " +
            "WHERE b.user.userId = :userId " +
            "AND b.dateOfBooking >= :now " +
            "ORDER BY b.dateOfBooking ASC, b.timeFrom ASC")
    List<Bookings> getUpcomingBookings(Integer userId, LocalDate now);

    @Query("SELECT b FROM Bookings b " +
            "WHERE b.user.userId = :userId " +
            "AND b.dateOfBooking < :now " +
            "ORDER BY b.dateOfBooking DESC, b.timeFrom DESC")
    List<Bookings> getHistoryOfBookings(Integer userId, LocalDate now);
}
