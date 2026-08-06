package com.thirupadham.booking;

import java.time.LocalDate;

public record BookingRequest(
        String guestName,
        String phone,
        LocalDate checkIn,
        LocalDate checkOut,
        String message
) {}
