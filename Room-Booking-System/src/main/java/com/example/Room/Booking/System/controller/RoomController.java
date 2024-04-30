package com.example.Room.Booking.System.controller;

import com.example.Room.Booking.System.model.Room;
import com.example.Room.Booking.System.repos.RoomRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class RoomController {
    @Autowired
    private RoomRepo roomRepo;

    @GetMapping("/rooms")
    public ResponseEntity<?> all_rooms(@RequestParam(defaultValue = "0") Integer capacity){
        try{
            if(capacity<0) {
                Map<String, String> payload = new HashMap<>();
                payload.put("Error","Invalid parameters");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(payload);
            }

            Optional<List<Room>> allRooms = roomRepo.findAllRoomsByCapacity(capacity);
            System.out.println(allRooms);

            //TODO

//       room <object>
//            -roomID<int>
//            -roomName<str>
//            -capacity<int>
//            -booked<object>
//                    -bookingID<int>
//                    -dateOfBooking<date>
//                    -timeFrom<str>
//                    -timeTo<str>
//                    -purpose<str>
//                    -user<object>
//                          -userID<int>


            //return ResponseEntity.status(HttpStatus.OK).body(allRooms.get());

            return new ResponseEntity<List<Room>>(allRooms.get(), HttpStatus.OK);

        }catch(Exception err){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Rooms Api Error");
        }
    }

    @PostMapping("/rooms")
    public ResponseEntity<?> add_room(@RequestBody Map<String,Object> room){
        try{
            String room_name = room.get("roomName").toString();
            Integer room_capacity = (Integer) room.get("roomCapacity");

            Optional<Room> found_room = roomRepo.findByRoomName(room_name);

            if(found_room.isPresent()){
                Map<String, String> payload = new HashMap<>();
                payload.put("Error","Room already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(payload);
            }

            if(room_capacity<=0){
                Map<String, String> payload = new HashMap<>();
                payload.put("Error","Invalid capacity");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(payload);
            }

            Room new_room = new Room();
            new_room.setRoomName(room_name);
            new_room.setRoomCapacity(room_capacity);

            Room saved_room = roomRepo.save(new_room);
            System.out.println(saved_room);

            return ResponseEntity.status(HttpStatus.OK).body("Room created successfully");

        }catch(Exception err){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Rooms Api Error");
        }
    }


    @PatchMapping("/rooms")
    public ResponseEntity<?> edit_room(@RequestBody Map<String,Object> room){
        try{
            Integer room_id = (Integer) room.get("roomID");
            String room_name = room.get("roomName").toString();
            Integer room_capacity = (Integer) room.get("roomCapacity");

            Optional<Room> found_room = roomRepo.findById(room_id);

            if(found_room.isEmpty()){
                Map<String, String> payload = new HashMap<>();
                payload.put("Error","Room does not exist");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(payload);
            }

            if(found_room.get().getRoomName().equals(room_name)){
                Map<String, String> payload = new HashMap<>();
                payload.put("Error","Room with given name already exists");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(payload);
            }

            if(room_capacity<0){
                Map<String, String> payload = new HashMap<>();
                payload.put("Error","Invalid capacity");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(payload);
            }

            Room edit_room = found_room.get();
            edit_room.setRoomName(room_name);
            edit_room.setRoomCapacity(room_capacity);

            Room saved_room = roomRepo.save(edit_room);
            System.out.println(saved_room);

            return ResponseEntity.status(HttpStatus.OK).body("Room edited successfully");

//            return ResponseEntity.ok("Login Successful");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//            return new ResponseEntity<Map<String,String>>(msg, HttpStatus.OK);
        }catch(Exception err){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err.toString());
        }
    }

    @DeleteMapping("/rooms")
    public ResponseEntity<?> delete_room(@RequestBody Map<String,Object> room){
        try{
            Integer room_id = (Integer) room.get("roomID");

            Optional<Room> found_room = roomRepo.findById(room_id);

            if(found_room.isEmpty()){
                Map<String, String> payload = new HashMap<>();
                payload.put("Error","Room does not exist");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(payload);
            }

            roomRepo.delete(found_room.get());

            return ResponseEntity.status(HttpStatus.OK).body("Room deleted successfully");

//            return ResponseEntity.ok("Login Successful");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//            return new ResponseEntity<Map<String,String>>(msg, HttpStatus.OK);
        }catch(Exception err){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Rooms Api Error");
        }
    }

}
