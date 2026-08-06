package com.thirupadham.notification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/notify")
public class NotificationController {

    private static final Logger log = LoggerFactory.getLogger(NotificationController.class);

    private final WhatsAppNotifier whatsAppNotifier;

    public NotificationController(WhatsAppNotifier whatsAppNotifier) {
        this.whatsAppNotifier = whatsAppNotifier;
    }

    // This is the endpoint the booking-service will call later, once a
    // guest submits a booking enquiry. For now we can test it directly
    // with curl before any other service exists.
    @PostMapping
    public Map<String, Object> notify(@RequestBody NotificationRequest request) {
        log.info("New booking notification received for guest={}", request.guestName());

        // In a real system this is where you'd call the WhatsApp Business
        // API, an SMS gateway, or send an email. For learning purposes we
        // just log it clearly to the console - the "channel" is simulated.
        System.out.println("=================================================");
        System.out.println(" NEW BOOKING ENQUIRY - Thirupadham Homestay");
        System.out.println(" Guest    : " + request.guestName());
        System.out.println(" Phone    : " + request.phone());
        System.out.println(" Check-in : " + request.checkIn());
        System.out.println(" Check-out: " + request.checkOut());
        System.out.println(" Message  : " + request.message());
        System.out.println(" Received : " + LocalDateTime.now());
        System.out.println("=================================================");

        String whatsappMessage = String.format(
                "New booking enquiry - Thirupadham Homestay%n" +
                "Guest: %s%nPhone: %s%nCheck-in: %s%nCheck-out: %s%nMessage: %s",
                request.guestName(), request.phone(), request.checkIn(), request.checkOut(), request.message()
        );
        whatsAppNotifier.send(whatsappMessage);

        return Map.of(
                "status", "notified",
                "channel", "console-log + whatsapp",
                "receivedAt", LocalDateTime.now().toString()
        );
    }

    // Simple endpoint to confirm the service is alive - useful now for a
    // manual check, and later becomes the basis for a Kubernetes health probe.
    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("status", "UP", "service", "notification-service");
    }
}
