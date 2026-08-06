package com.thirupadham.web;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class AirbnbCalendarService {

    private static final Logger log = LoggerFactory.getLogger(AirbnbCalendarService.class);
    private static final DateTimeFormatter ICAL_DATE = DateTimeFormatter.BASIC_ISO_DATE; // yyyyMMdd

    @Value("${airbnb.ical-url:}")
    private String icalUrl;

    private final RestTemplate restTemplate;

    // volatile so the scheduled writer thread and web request reader
    // threads always see the latest completed sync, without needing a lock.
    private volatile List<DateRange> blockedRanges = List.of();

    public AirbnbCalendarService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        refresh();
    }

    // Airbnb's calendar doesn't change minute-to-minute, and hitting their
    // servers on every single homepage visit would be unnecessary load for
    // no real benefit - a periodic refresh is the right pattern here.
    @Scheduled(fixedRate = 30 * 60 * 1000)
    public void refresh() {
        if (icalUrl == null || icalUrl.isBlank()) {
            log.info("No Airbnb iCal URL configured yet - calendar will show all dates as open.");
            return;
        }
        try {
            String ics = restTemplate.getForObject(icalUrl, String.class);
            blockedRanges = parse(ics);
            log.info("Synced {} blocked date range(s) from Airbnb.", blockedRanges.size());
        } catch (Exception e) {
            log.warn("Could not refresh Airbnb calendar - keeping last known data: {}", e.getMessage());
        }
    }

    private List<DateRange> parse(String ics) {
        List<DateRange> ranges = new ArrayList<>();
        if (ics == null) return ranges;

        LocalDate start = null;
        LocalDate end = null;

        for (String line : ics.split("\\r?\\n")) {
            if (line.startsWith("BEGIN:VEVENT")) {
                start = null;
                end = null;
            } else if (line.startsWith("DTSTART")) {
                start = extractDate(line);
            } else if (line.startsWith("DTEND")) {
                end = extractDate(line);
            } else if (line.startsWith("END:VEVENT")) {
                if (start != null && end != null) {
                    ranges.add(new DateRange(start, end));
                }
            }
        }
        return ranges;
    }

    // A line looks like "DTSTART;VALUE=DATE:20260810" - we only need the
    // 8 digits after the last colon.
    private LocalDate extractDate(String line) {
        int colon = line.lastIndexOf(':');
        if (colon == -1 || line.length() < colon + 9) return null;
        try {
            return LocalDate.parse(line.substring(colon + 1, colon + 9), ICAL_DATE);
        } catch (Exception e) {
            return null;
        }
    }

    public boolean isBlocked(LocalDate date) {
        for (DateRange range : blockedRanges) {
            if (range.contains(date)) return true;
        }
        return false;
    }
}
