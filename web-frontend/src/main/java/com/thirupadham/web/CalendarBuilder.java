package com.thirupadham.web;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;

@Component
public class CalendarBuilder {

    private final AirbnbCalendarService calendarService;

    public CalendarBuilder(AirbnbCalendarService calendarService) {
        this.calendarService = calendarService;
    }

    public CalendarMonth build(YearMonth yearMonth) {
        LocalDate firstOfMonth = yearMonth.atDay(1);
        // Sunday-first grid, matching Airbnb's own calendar layout.
        // DayOfWeek.getValue(): Monday=1 ... Sunday=7, so "% 7" turns
        // Sunday into 0 leading blanks, Monday into 1, and so on.
        int leadingBlanks = firstOfMonth.getDayOfWeek().getValue() % 7;
        LocalDate today = LocalDate.now();

        List<CalendarDay> days = new ArrayList<>();
        for (int i = 0; i < leadingBlanks; i++) {
            days.add(new CalendarDay(0, null, false, false, false));
        }
        for (int d = 1; d <= yearMonth.lengthOfMonth(); d++) {
            LocalDate date = yearMonth.atDay(d);
            days.add(new CalendarDay(d, date, true, calendarService.isBlocked(date), date.isBefore(today)));
        }
        while (days.size() % 7 != 0) {
            days.add(new CalendarDay(0, null, false, false, false));
        }

        List<List<CalendarDay>> weeks = new ArrayList<>();
        for (int i = 0; i < days.size(); i += 7) {
            weeks.add(days.subList(i, i + 7));
        }

        String label = yearMonth.getMonth().getDisplayName(TextStyle.FULL, LocaleContextHolder.getLocale()) + " " + yearMonth.getYear();
        return new CalendarMonth(label, weeks);
    }
}
