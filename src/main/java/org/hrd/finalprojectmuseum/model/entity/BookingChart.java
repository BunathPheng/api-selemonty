package org.hrd.finalprojectmuseum.model.entity;

import lombok.Data;

@Data
public class BookingChart {
    private String month;
    private Integer tours;
    private Integer individuals;
    private String fullMonth;
}
