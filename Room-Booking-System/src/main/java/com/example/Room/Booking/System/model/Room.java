package com.example.Room.Booking.System.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="rooms")
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer roomId;
    @Column(unique = true)
    private String roomName;
    private Integer roomCapacity;
    // Additional fields as needed
}