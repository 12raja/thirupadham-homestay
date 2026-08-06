package com.thirupadham.booking;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
public class NotificationClient {

    private static final Logger log = LoggerFactory.getLogger(NotificationClient.class);

    private final RestTemplate restTemplate;

    // This URL points at notification-service's Kubernetes Service name, not
    // an IP address - Kubernetes' internal DNS resolves it automatically,
    // the same way you tested with the temporary curl pod earlier.
    @Value("${notification.service.url}")
    private String notificationServiceUrl;

    public NotificationClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void notifyNewBooking(Booking booking) {
        try {
            Map<String, String> payload = Map.of(
                    "guestName", booking.getGuestName(),
                    "phone", booking.getPhone(),
                    "checkIn", booking.getCheckIn().toString(),
                    "checkOut", booking.getCheckOut().toString(),
                    "message", booking.getMessage() == null ? "" : booking.getMessage()
            );
            restTemplate.postForObject(notificationServiceUrl, payload, Map.class);
        } catch (Exception e) {
            // A notification hiccup should never undo an already-saved booking.
            // We log it so it can be investigated, but the guest's booking
            // stays safely recorded in Postgres either way.
            log.warn("Could not reach notification-service: {}", e.getMessage());
        }
    }
}
