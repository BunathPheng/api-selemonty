package org.hrd.finalprojectmuseum.model.dto.response;

import lombok.Data;
import org.hrd.finalprojectmuseum.model.entity.Pagination;

import java.util.List;

@Data
public class ListResponse<T> {
    List<T> items;
    Pagination pagination;
}