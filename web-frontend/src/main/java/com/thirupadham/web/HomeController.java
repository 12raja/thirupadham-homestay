package com.thirupadham.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.client.RestTemplate;

import java.time.YearMonth;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private static final Logger log = LoggerFactory.getLogger(HomeController.class);

    private static final List<String> WEEKDAY_KEYS = List.of(
            "weekday.sun", "weekday.mon", "weekday.tue", "weekday.wed",
            "weekday.thu", "weekday.fri", "weekday.sat"
    );

    private final RestTemplate restTemplate;
    private final PhotoStorage photoStorage;
    private final CalendarBuilder calendarBuilder;
    private final MessageSource messageSource;

    // Points at booking-service's Kubernetes Service name - same pattern
    // used for notification-service inside booking-service itself.
    @Value("${booking.service.url}")
    private String bookingServiceUrl;

    // A placeholder number ships by default so the site never shows a real
    // wrong number - replace HOMESTAY_WHATSAPP_NUMBER with the real one
    // via an environment variable before sharing this site with guests.
    @Value("${homestay.whatsapp-number}")
    private String whatsappNumber;

    @Value("${airbnb.listing-url:}")
    private String airbnbUrl;

    public HomeController(RestTemplate restTemplate, PhotoStorage photoStorage, CalendarBuilder calendarBuilder,
                           MessageSource messageSource) {
        this.restTemplate = restTemplate;
        this.photoStorage = photoStorage;
        this.calendarBuilder = calendarBuilder;
        this.messageSource = messageSource;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("booking", new BookingFormModel());
        model.addAttribute("whatsappNumber", whatsappNumber);
        model.addAttribute("airbnbUrl", airbnbUrl);
        model.addAttribute("galleryPhotos", photoStorage.listLatest(12));
        model.addAttribute("todayDate", java.time.LocalDate.now().toString());

        YearMonth thisMonth = YearMonth.now();
        model.addAttribute("months", List.of(
                calendarBuilder.build(thisMonth),
                calendarBuilder.build(thisMonth.plusMonths(1))
        ));
        model.addAttribute("weekdayLabels", WEEKDAY_KEYS.stream()
                .map(key -> messageSource.getMessage(key, null, LocaleContextHolder.getLocale()))
                .toList());

        return "index";
    }

    @SuppressWarnings("unchecked")
    @PostMapping("/book")
    public String submitBooking(@ModelAttribute("booking") BookingFormModel booking, Model model) {
        Map<String, String> payload = Map.of(
                "guestName", safe(booking.getGuestName()),
                "phone", safe(booking.getPhone()),
                "checkIn", safe(booking.getCheckIn()),
                "checkOut", safe(booking.getCheckOut()),
                "message", safe(booking.getMessage())
        );

        try {
            Map<String, Object> response = restTemplate.postForObject(bookingServiceUrl, payload, Map.class);
            model.addAttribute("bookingId", response != null ? response.get("id") : null);
            model.addAttribute("guestName", booking.getGuestName());
            model.addAttribute("checkIn", booking.getCheckIn());
            model.addAttribute("checkOut", booking.getCheckOut());
            model.addAttribute("whatsappNumber", whatsappNumber);
            return "booking-confirmation";
        } catch (Exception e) {
            log.warn("Could not reach booking-service: {}", e.getMessage());
            model.addAttribute("whatsappNumber", whatsappNumber);
            return "booking-error";
        }
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }
}
