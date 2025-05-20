package org.hrd.finalprojectmuseum.repository.museum;

import org.apache.ibatis.annotations.*;
import org.hrd.finalprojectmuseum.model.dto.request.museum_owner.MuseumZoneRequest;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZone;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumZoneCategory;

import java.util.List;

@Mapper
public interface MuseumZoneRepository {

    @Select("""
        SELECT zone_category_id, name FROM zone_categories;
    """)
    @Results(id = "zoneCategory", value = {
            @Result(property = "museumZoneCategoryId", column = "zone_category_id"),
    })
    List<MuseumZoneCategory> getAllMuseumZoneCategories();

    @Insert("""
        INSERT INTO museum_zone()
    """)
    void createMuseumZone(MuseumZoneRequest museumZoneRequest);
}
