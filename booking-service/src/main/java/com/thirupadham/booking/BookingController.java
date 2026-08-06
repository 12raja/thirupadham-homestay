package com.thirupadham.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingRepository bookingRepository;
    private final NotificationClient notificationClient;

    public BookingController(BookingRepository bookingRepository, NotificationClient notificationClient) {
        this.bookingRepository = bookingRepository;
        this.notificationClient = notificationClient;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Booking createBooking(@RequestBody BookingRequest request) {
        Booking booking = new Booking();
        booking.setGuestName(request.guestName());
        booking.setPhone(request.phone());
        booking.setCheckIn(request.checkIn());
        booking.setCheckOut(request.checkOut());
        booking.setMessage(request.message());

        // Save to Postgres FIRST, notify SECOND - order matters. A guest's
        // booking must never be lost just because notification-service
        // happens to be briefly unavailable.
        Booking saved = bookingRepository.save(booking);
        notificationClient.notifyNewBooking(saved);

        return saved;
    }

    @GetMapping
    public List<Booking> listBookings() {
        return bookingRepository.findAll();
    }

    @GetMapping("/health")
    public String health() {
        return "UP";
    }
}
