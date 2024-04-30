package com.example.Room.Booking.System.repos;

import com.example.Room.Booking.System.model.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepo extends JpaRepository<Room, Integer>{
    @Query("SELECT r FROM Room r WHERE r.roomCapacity >= :capacity ORDER BY roomCapacity DESC")
    Optional<List<Room>> findAllRoomsByCapacity(Integer capacity);

    @Query("SELECT r FROM Room r WHERE r.roomName=:roomName")
    Optional<Room> findByRoomName(String roomName);
}
