package com.example.Room.Booking.System.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name="bookings")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Bookings {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer bookingId;

    @ManyToOne
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "roomId", referencedColumnName = "roomId")
    private Room room;

    private LocalDate dateOfBooking;
    private String timeFrom;
    private String timeTo;
    private String purpose;
    // Additional fields as needed
}
