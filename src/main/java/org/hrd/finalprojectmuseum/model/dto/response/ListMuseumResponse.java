package org.hrd.finalprojectmuseum.model.dto.response;

import lombok.Data;
import org.hrd.finalprojectmuseum.model.entity.Pagination;
import org.hrd.finalprojectmuseum.model.entity.museum_owner.MuseumShortInfo;

import java.util.List;

@Data
public class ListMuseumResponse {
    List<MuseumShortInfo> museums;
    Pagination pagination;
}