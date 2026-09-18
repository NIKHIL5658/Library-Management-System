package com.library.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public final class FineCalculator {

    private static final double FINE_PER_DAY = 5.0;
    private static final int GRACE_PERIOD_DAYS = 0;

    private FineCalculator() {

    }

    public static double calculateFine(LocalDate dueDate, LocalDate returnOrReferenceDate) {
        long daysLate = ChronoUnit.DAYS.between(dueDate, returnOrReferenceDate) - GRACE_PERIOD_DAYS;
        if (daysLate <= 0) {
            return 0.0;
        }
        return daysLate * FINE_PER_DAY;
    }
}
