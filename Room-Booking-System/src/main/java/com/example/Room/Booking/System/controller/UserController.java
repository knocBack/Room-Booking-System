package com.example.Room.Booking.System.controller;

import com.example.Room.Booking.System.model.Bookings;
import com.example.Room.Booking.System.model.User;
import com.example.Room.Booking.System.repos.BookingRepo;
import com.example.Room.Booking.System.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
public class UserController {
    @Autowired
    private UserRepo userRepo;
    @Autowired
    private BookingRepo bookingRepo;

//    @PostMapping("/login")
//    public ResponseEntity<Map<String, String>> userLogin(@RequestBody Map<String, String> requestBody) {
//        String email = requestBody.get("email");
//        String password = requestBody.get("password");
//
//        try {
//            // Perform login authentication logic here (e.g., validate credentials)
//            // Assuming authentication is successful, return success response
//            Map<String, String> response = new HashMap<>();
//            response.put("message", "Login Successful");
//            return ResponseEntity.ok(response);
//        } catch (Exception err) {
//            // Return error response if an exception occurs during login process
//            Map<String, String> errorResponse = new HashMap<>();
//            errorResponse.put("message", "Internal Server Error");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
//        }
//    }

    @PostMapping("/login")
    public ResponseEntity<?> user_login(@RequestBody Map<String,String> user){
        try{
            String email = user.get("email");
            String password = user.get("password");

            Optional<User> userObj = userRepo.findByEmail(email);
            System.out.println(userObj);

            Map<String, String> payload = new HashMap<>();
            if(userObj.isEmpty()) {
                payload.put("Error","User does not exist");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(payload);
            }
            else{
                User foundUser = userObj.get();
                if(foundUser.getPassword().equals(password))
                    return ResponseEntity.status(HttpStatus.OK).body("Login Successful");
                else{
                    payload.put("Error","Username/Password Incorrect");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(payload);
                }
            }
//            return ResponseEntity.ok("Login Successful");
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect password");
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
//            return new ResponseEntity<Map<String,String>>(msg, HttpStatus.OK);
        }catch(Exception err){
//            Map<String, String> payload = new HashMap<>();
//            payload.put("Error", "Internal Server Error");
//            return new ResponseEntity<Map<String,String>>(payload, HttpStatus.INTERNAL_SERVER_ERROR);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Login Api Error");
        }
    }

//    @PostMapping("/login")
//    public ResponseEntity<Map<String, String>> userLogin(@RequestBody Map<String, String> user) {
//        try {
//            String email = user.get("email");
//            String password = user.get("password");
//
//            // Implement your authentication logic here (e.g., check credentials)
//            // For demonstration, assume user_id is provided and fetched from user map
//            int userId = Integer.parseInt(user.get("user_id")); // Assuming user_id is provided
//
//            Optional<User> userObj = userRepo.findById(userId);
//            if (userObj.isPresent()) {
//                // User exists in the database, perform authentication logic (e.g., validate password)
//                // Simulating successful login for demonstration
//                Map<String, String> payload = new HashMap<>();
//                payload.put("msg", "Login Successful");
//                return ResponseEntity.ok(payload);
//            } else {
//                // User not found based on provided user_id
//                Map<String, String> payload = new HashMap<>();
//                payload.put("msg", "User not found");
//                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(payload);
//            }
//        } catch (NumberFormatException e) {
//            // Handle number format exception (e.g., invalid user_id format)
//            Map<String, String> payload = new HashMap<>();
//            payload.put("msg", "Invalid user_id format");
//            return ResponseEntity.badRequest().body(payload);
//        } catch (Exception e) {
//            // Handle other exceptions (e.g., database errors, internal server errors)
//            Map<String, String> payload = new HashMap<>();
//            payload.put("msg", "Internal Server Error");
//            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(payload);
//        }
//    }

    @PostMapping("/signup")
    public ResponseEntity<?> user_signup(@RequestBody Map<String,String> user){
        try{
            String email = user.get("email");
            String name = user.get("name");
            String password = user.get("password");

            Optional<User> existingUser = userRepo.findByEmail(email);
            if(existingUser.isPresent()) {
                Map<String, String> payload = new HashMap<>();
                payload.put("Error","Forbidden, Account already exists");
                return ResponseEntity.status(HttpStatus.CONFLICT).body(payload);
            }

            User userObj = new User();
            userObj.setEmail(email);
            userObj.setName(name);
            userObj.setPassword(password);
            System.out.println(userObj);

            User saved_obj = userRepo.save(userObj);
            System.out.println(saved_obj);

            return ResponseEntity.status(HttpStatus.OK).body("Account Creation Successful");

        }catch(Exception err){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Sign Up Api Error");
        }
    }

    @GetMapping("/users")
    public ResponseEntity<?> user_details() {
        List<User> allUsers = userRepo.findAll();
        List<Map<String, Object>> payload = new ArrayList<>();
        Map<String, Object> temp = new HashMap<>();
        for(User user : allUsers){
            temp.put("userID", user.getUserId());
            temp.put("name", user.getName());
            temp.put("email", user.getEmail());
            payload.add(temp);
        }
        return ResponseEntity.status(HttpStatus.OK).body(payload);
    }
    @GetMapping("/user")
    public ResponseEntity<?> user_details(@RequestParam Integer userID) {
        Optional<User> found_user = userRepo.findById(userID);
        if (found_user.isPresent()) {
            User user = found_user.get();
            Map<String, Object> temp = new HashMap<>();
            temp.put("userID", user.getUserId());
            temp.put("name", user.getName());
            temp.put("email", user.getEmail());
            return ResponseEntity.status(HttpStatus.OK).body(temp);
        } else {
            Map<String, String> payload = new HashMap<>();
            payload.put("Error","User does not exist");
            return ResponseEntity.status(HttpStatus.CONFLICT).body(payload);
        }
    }

    // for history, a single filter using time, >= and <=

    @GetMapping("/history")
    public ResponseEntity<?> getUserBookingHistory(@RequestParam("userID") Integer userId) {
        try{
            Optional<User> found_user = userRepo.findById(userId);
            if (found_user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
            }

            List<Bookings> historyOfBookings = bookingRepo.getHistoryOfBookings(userId, LocalDate.now());
            List<Map<String, Object>> all_bookings = new ArrayList<>();
            Map<String, Object> temp = new HashMap<>();
            Map<String, Object> temp_user = new HashMap<>();

            for(Bookings book : historyOfBookings){
                temp = new HashMap<>();
                temp_user = new HashMap<>();
                temp.put("bookingID", book.getBookingId());
                temp.put("dateOfBooking", book.getDateOfBooking());
                temp.put("timeFrom", book.getTimeFrom());
                temp.put("timeTo", book.getTimeTo());
                temp.put("purpose", book.getPurpose());
                temp_user.put("userID", book.getUser().getUserId());
                temp.put("user", temp_user);
                all_bookings.add(temp);
            }
            return ResponseEntity.status(HttpStatus.OK).body(all_bookings);
        }
        catch(Exception err){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err.toString());
        }
    }

    @GetMapping("/upcoming")
    public ResponseEntity<?> getUserUpcomingBooking(@RequestParam("userID") Integer userId) {
        try{
            Optional<User> userOptional = userRepo.findById(userId);
            if (userOptional.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User does not exist");
            }

            //findFirstByUserIdAndDateOfBookingGreaterThanOrderByDateOfBookingAsc
            List<Bookings> upcomingBooking = bookingRepo.getUpcomingBookings(userId, LocalDate.now());
            List<Map<String, Object>> all_bookings = new ArrayList<>();
            Map<String, Object> temp = new HashMap<>();
            Map<String, Object> temp_user = new HashMap<>();
//            {
//                "bookingID": 1,
//                    "dateOfBooking": "2024-04-30",
//                    "timeFrom": "13:00:00",
//                    "timeTo": "15:00:00",
//                    "purpose": "Fun",
//                    "user": {
//                          "userID": 1
//                      }
//            }
            for(Bookings book : upcomingBooking){
                temp = new HashMap<>();
                temp_user = new HashMap<>();
                temp.put("bookingID", book.getBookingId());
                temp.put("dateOfBooking", book.getDateOfBooking());
                temp.put("timeFrom", book.getTimeFrom());
                temp.put("timeTo", book.getTimeTo());
                temp.put("purpose", book.getPurpose());
                temp_user.put("userID", book.getUser().getUserId());
                temp.put("user", temp_user);
                all_bookings.add(temp);
            }
            return ResponseEntity.status(HttpStatus.OK).body(all_bookings);
        }
        catch(Exception err){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err.toString());
        }
    }

}
