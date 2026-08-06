package com.thirupadham.booking;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

// @Entity tells Spring/Hibernate: "this class maps to a database table."
// With spring.jpa.hibernate.ddl-auto=update (set in application.properties),
// Hibernate will create the "bookings" table automatically the first time
// this app starts - no manual SQL needed for a learning project like this.
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String guestName;
    private String phone;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private String message;
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public LocalDate getCheckIn() { return checkIn; }
    public void setCheckIn(LocalDate checkIn) { this.checkIn = checkIn; }

    public LocalDate getCheckOut() { return checkOut; }
    public void setCheckOut(LocalDate checkOut) { this.checkOut = checkOut; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
