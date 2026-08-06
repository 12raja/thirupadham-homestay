package com.thirupadham.web;

import java.util.List;

public record CalendarMonth(String label, List<List<CalendarDay>> weeks) {}
