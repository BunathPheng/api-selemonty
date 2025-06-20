package org.hrd.finalprojectmuseum.model.entity.museum_owner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteMuseumSchedule {
    private String day;
    private LocalTime openingTime;
    private LocalTime closingTime;
}
