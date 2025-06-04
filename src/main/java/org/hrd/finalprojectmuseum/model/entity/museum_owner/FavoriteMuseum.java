package org.hrd.finalprojectmuseum.model.entity.museum_owner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FavoriteMuseum {
    private UUID museumId;
    private String museumName;
    private String museumEmail;
    private String logoLink;
    private String museumAddress;
    private Boolean isFavorite;
    private List<FavoriteMuseumSchedule> favoriteMuseumSchedule;
}
