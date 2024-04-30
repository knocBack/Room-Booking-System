package com.example.Room.Booking.System.controller;

import com.example.Room.Booking.System.model.Room;
import com.example.Room.Booking.System.model.Bookings;
import com.example.Room.Booking.System.model.User;
import com.example.Room.Booking.System.repos.BookingRepo;
import com.example.Room.Booking.System.repos.RoomRepo;
import com.example.Room.Booking.System.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.awt.print.Book;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController

public class BookingController {
    @Autowired
    private BookingRepo bookingRepo;
    @Autowired
    private RoomRepo roomRepo;
    @Autowired
    private UserRepo userRepo;


    private static boolean isValidTimeFormat(String time) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
            LocalTime parsedTime = LocalTime.parse(time, formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }

    private boolean isRoomAvailable(Integer roomId, LocalDate dateOfBooking, String timeFrom, String timeTo) {
        LocalTime requestedTimeFrom = LocalTime.parse(timeFrom);
        LocalTime requestedTimeTo = LocalTime.parse(timeTo);

        List<Bookings> dayBookings = bookingRepo.getAllBookingsOn(roomId, dateOfBooking);

        for (Bookings booking : dayBookings) {
            LocalTime existingTimeFrom = LocalTime.parse(booking.getTimeFrom());
            LocalTime existingTimeTo = LocalTime.parse(booking.getTimeTo());

            if (doTimeRangesOverlap(requestedTimeFrom, requestedTimeTo, existingTimeFrom, existingTimeTo)) {
                return false;
            }
        }
        return true;
    }

    private boolean doTimeRangesOverlap(LocalTime from1, LocalTime to1, LocalTime from2, LocalTime to2) {
        return !from1.isAfter(to2) && !from2.isAfter(to1);
    }


    @PostMapping("/book")
    public ResponseEntity<?> add_booking(@RequestBody Map<String,Object> body){
        try{
//            {
//                    "roomID": "34",
//                    "userID": "4",
//                    "dateOfBooking": "2024-04-30",
//                    "timeFrom": "13:00",
//                    "timeTo": "15:00",
//                    "purpose": "Fun"
//
//            }
            Integer user_id = (Integer) body.get("userID");
            Integer room_id = (Integer) body.get("roomID");
//            LocalDate.parse(dateOfBookingStr)
            LocalDate date_of_booking = LocalDate.parse(body.get("dateOfBooking").toString());
            String time_from = body.get("timeFrom").toString();
            String time_to = body.get("timeTo").toString();
            String purpose = body.get("purpose").toString();

            Map<String, String> payload = new HashMap<>();

            Optional<User> found_user = userRepo.findById(user_id);
            if(found_user.isEmpty()){
                payload.put("Error","User does not exist");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(payload);
            }

            Optional<Room> found_room = roomRepo.findById(room_id);
            if(found_room.isEmpty()){
                payload.put("Error","Room does not exist");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(payload);
            }

            boolean isFutureDate = date_of_booking.isAfter(LocalDate.now());
            System.out.println(isFutureDate);
            boolean isValidTimeFormat = isValidTimeFormat(time_from) && isValidTimeFormat(time_to);
            if (isFutureDate && isValidTimeFormat) {

                if(isRoomAvailable(room_id, date_of_booking, time_from, time_to)){
                    Bookings new_booking = new Bookings();
                    new_booking.setUser(found_user.get());
                    new_booking.setRoom(found_room.get());
                    new_booking.setDateOfBooking(date_of_booking);
                    new_booking.setTimeFrom(time_from);
                    new_booking.setTimeTo(time_to);
                    new_booking.setPurpose(purpose);

                    Bookings saved_booking = bookingRepo.save(new_booking);
                    System.out.println(saved_booking);

                    return ResponseEntity.status(HttpStatus.OK).body("Booking created successfully");
                }
                else{
                    return ResponseEntity.status(HttpStatus.OK).body("Room unavailable");
                }

            } else {
                return ResponseEntity.status(HttpStatus.OK).body("Invalid date/time");
            }

//            Room new_room = new Room();
//            new_room.setRoomName(room_name);
//            new_room.setRoomCapacity(room_capacity);
//
//            Room saved_room = roomRepo.save(new_room);
//            System.out.println(saved_room);
//
//            return ResponseEntity.status(HttpStatus.OK).body("Room created successfully");

        }catch(Exception err){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err.toString());
        }
    }

    @PatchMapping("/book")
    public ResponseEntity<?> edit_booking(@RequestBody Map<String,Object> body){
        try{
//            {
//                    "roomID": "1",
//                    "userID": "1",
//                    "bookingID":"1",
//                    "dateOfBooking": "2024-04-30",
//                    "timeFrom": "13:00:00",
//                    "timeTo": "15:00:00",
//                    "purpose": "Fun"
//            }
            Integer user_id = (Integer) body.get("userID");
            Integer room_id = (Integer) body.get("roomID");
            Integer booking_id = (Integer) body.get("bookingID");
            LocalDate date_of_booking = LocalDate.parse(body.get("dateOfBooking").toString());
            String time_from = body.get("timeFrom").toString();
            String time_to = body.get("timeTo").toString();
            String purpose = body.get("purpose").toString();

            Map<String, Object> payload = new HashMap<>();

            Optional<User> found_user = userRepo.findById(user_id);
            if(found_user.isEmpty()){
                payload.put("Error","User does not exist");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(payload);
            }

            Optional<Room> found_room = roomRepo.findById(room_id);
            if(found_room.isEmpty()){
                payload.put("Error","Room does not exist");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(payload);
            }

            Optional<Bookings> found_booking = bookingRepo.findById(booking_id);
            if(found_booking.isEmpty()){
                payload.put("Error","Booking does not exist");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(payload);
            }

            boolean isFutureDate = date_of_booking.isAfter(LocalDate.now());
            boolean isValidTimeFormat = isValidTimeFormat(time_from) && isValidTimeFormat(time_to);
            if (isFutureDate && isValidTimeFormat) {

                if(isRoomAvailable(room_id, date_of_booking, time_from, time_to)){
                    Bookings new_booking = found_booking.get();
                    new_booking.setUser(found_user.get());
                    new_booking.setRoom(found_room.get());
                    new_booking.setDateOfBooking(date_of_booking);
                    new_booking.setTimeFrom(time_from);
                    new_booking.setTimeTo(time_to);
                    new_booking.setPurpose(purpose);

                    Bookings saved_booking = bookingRepo.save(new_booking);
                    System.out.println(saved_booking);

                    return ResponseEntity.status(HttpStatus.OK).body("Booking modified successfully");
                }
                else{
                    return ResponseEntity.status(HttpStatus.OK).body("Room unavailable");
                }

            } else {
                return ResponseEntity.status(HttpStatus.OK).body("Invalid date/time");
            }

//            Room new_room = new Room();
//            new_room.setRoomName(room_name);
//            new_room.setRoomCapacity(room_capacity);
//
//            Room saved_room = roomRepo.save(new_room);
//            System.out.println(saved_room);
//
//            return ResponseEntity.status(HttpStatus.OK).body("Room created successfully");

        }catch(Exception err){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Rooms Patch Api Error");
        }
    }

    @DeleteMapping("/book")
    public ResponseEntity<?> delete_booking(@RequestParam Integer bookingId){
        try{

            Optional<Bookings> found_booking = bookingRepo.findById(bookingId);

            if(found_booking.isEmpty()){
                Map<String, String> payload = new HashMap<>();
                payload.put("Error","Booking does not exist");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(payload);
            }

            bookingRepo.delete(found_booking.get());

            return ResponseEntity.status(HttpStatus.OK).body("Booking deleted successfully");

//            return ResponseEntity.ok("Login Successful");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//            return new ResponseEntity<Map<String,String>>(msg, HttpStatus.OK);
        }catch(Exception err){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Rooms Api Error");
        }
    }

}
