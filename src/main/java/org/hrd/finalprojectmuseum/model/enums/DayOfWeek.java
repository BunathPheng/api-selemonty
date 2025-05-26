package org.hrd.finalprojectmuseum.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DayOfWeek {
    MONDAY("Monday"),
    TUESDAY("Tuesday"),
    WEDNESDAY("Wednesday"),
    THURSDAY("Thursday"),
    FRIDAY("Friday"),
    SATURDAY("Saturday"),
    SUNDAY("Sunday");

    private final String value;

    DayOfWeek(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @JsonCreator
    public static DayOfWeek fromValue(String value) {
        for (DayOfWeek day : DayOfWeek.values()) {
            if (day.value.equalsIgnoreCase(value) || day.name().equalsIgnoreCase(value)) {
                return day;
            }
        }
        throw new IllegalArgumentException("Invalid day of week: " + value);
    }

    public String toDatabaseValue() {
        return this.value;
    }

    public static DayOfWeek fromDatabaseValue(String dbValue) {
        return fromValue(dbValue);
    }

    @Override
    public String toString() {
        return this.value;
    }
}