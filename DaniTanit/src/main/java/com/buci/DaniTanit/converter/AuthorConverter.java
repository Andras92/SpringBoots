package com.buci.DaniTanit.converter;

import com.buci.DaniTanit.dao.BookRepository;
import com.buci.DaniTanit.dto.AuthorDto;
import com.buci.DaniTanit.entities.Author;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class AuthorConverter {

    public AuthorDto toDto(Author entity) {
        AuthorDto dto = new AuthorDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setBooks(entity.getBooks().size());
        return dto;
    }

    public Author toEntity(AuthorDto dto) {
        Author entity = new Author();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setBooks(new ArrayList<>());
        return entity;
    }
}
