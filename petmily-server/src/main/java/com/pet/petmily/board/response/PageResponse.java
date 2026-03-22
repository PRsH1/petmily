package com.pet.petmily.board.response;

import lombok.Getter;

import java.util.List;

@Getter
public class PageResponse<T> {
    private final String status;
    private final String message;
    private final List<T> data;
    private final int currentPage;
    private final int totalPages;
    private final long totalElements;
    private final int size;

    public PageResponse(String status, String message, List<T> data,
                        int currentPage, int totalPages, long totalElements, int size) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.size = size;
    }
}
