package org.hrd.finalprojectmuseum.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DayOfWeek {
    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY;

    @JsonCreator
    public static DayOfWeek fromValue(String value) {
        for (DayOfWeek day : DayOfWeek.values()) {
            if (day.name().equalsIgnoreCase(value)) {
                return day;
            }
        }
        throw new IllegalArgumentException("Invalid day of week: " + value);
    }

    public String toDatabaseValue() {
        return this.name();
    }

    public static DayOfWeek fromDatabaseValue(String dbValue) {
        return fromValue(dbValue);
    }

    @Override
    public String toString() {
        return this.name();
    }
}