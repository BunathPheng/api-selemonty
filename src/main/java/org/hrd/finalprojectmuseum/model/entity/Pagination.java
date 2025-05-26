package org.hrd.finalprojectmuseum.model.entity;

import lombok.Builder;
import lombok.Data;

@Data
public class Pagination {
    private Integer totalItems;
    private Integer totalPages;
    private Integer currentPage;
    private Integer pageSize;
    private Integer hasNextPage;
    private Integer hasPreviousPage;
    private Integer nextPage;
    private Integer previousPage;
    private Integer firstPage;
    private Integer lastPage;

    public Pagination calculatePagination(Integer totalItems, Integer page, Integer size) {
        Pagination pagination = new Pagination();

        int totalPages = (int) Math.ceil((double) totalItems / size);
        int currentPage = Math.max(1, Math.min(page, totalPages));
        int hasNextPage = currentPage < totalPages ? 1 : 0;
        int hasPreviousPage = currentPage > 1 ? 1 : 0;

        pagination.setTotalItems(totalItems);
        pagination.setPageSize(size);
        pagination.setTotalPages(totalPages);
        pagination.setCurrentPage(currentPage);
        pagination.setHasNextPage(hasNextPage);
        pagination.setHasPreviousPage(hasPreviousPage);
        pagination.setNextPage(hasNextPage == 1 ? currentPage + 1 : null);
        pagination.setPreviousPage(hasPreviousPage == 1 ? currentPage - 1 : null);
        pagination.setFirstPage(totalPages > 0 ? 1 : null);
        pagination.setLastPage(totalPages > 0 ? totalPages : null);

        return pagination;
    }
}
