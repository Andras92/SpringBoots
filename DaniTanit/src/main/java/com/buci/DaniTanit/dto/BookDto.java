package com.buci.DaniTanit.dto;

import lombok.Data;

@Data
public class BookDto {
    private Long id;

    private String name;

    private Integer publishYear;

    private Long authorId;
}

