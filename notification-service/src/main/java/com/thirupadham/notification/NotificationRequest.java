package com.thirupadham.notification;

// A "record" is Java's shorthand for a simple data holder - this one
// describes exactly what JSON shape this service expects to receive.
// No getters/setters to write by hand; Java generates them for us.
public record NotificationRequest(
        String guestName,
        String phone,
        String checkIn,
        String checkOut,
        String message
) {}
