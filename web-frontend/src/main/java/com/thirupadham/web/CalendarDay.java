package com.thirupadham.web;

import java.time.LocalDate;

public record CalendarDay(int dayOfMonth, LocalDate date, boolean inMonth, boolean blocked, boolean past) {}
