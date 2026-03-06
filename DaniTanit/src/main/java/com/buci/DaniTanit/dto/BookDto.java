package com.buci.DaniTanit.dto;

import lombok.Data;

import java.util.List;

@Data
public class BookDto {
    private Long id;

    private String name;

    private Integer publishYear;

    private Long authorId;

    private List<Long> genreIds;
}

