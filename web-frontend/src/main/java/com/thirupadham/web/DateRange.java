package com.thirupadham.web;

import java.time.LocalDate;

// Airbnb's calendar convention: DTEND is the checkout date, which is
// exclusive - the night of "end" itself is NOT blocked (a new guest can
// check in that day). Hence "date < end", not "date <= end".
public record DateRange(LocalDate start, LocalDate end) {
    public boolean contains(LocalDate date) {
        return !date.isBefore(start) && date.isBefore(end);
    }
}
