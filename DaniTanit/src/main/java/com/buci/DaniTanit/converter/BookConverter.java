package com.buci.DaniTanit.converter;

import com.buci.DaniTanit.dao.AuthorRepository;
import com.buci.DaniTanit.dto.BookDto;
import com.buci.DaniTanit.entities.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BookConverter {
    private final AuthorRepository authorRepository;

    public BookDto toDto(Book entity) {
        BookDto dto = new BookDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setAuthorId(entity.getAuthor().getId());
        dto.setPublishYear(entity.getPublishYear());
        return dto;
    }

    public Book toEntity(BookDto dto) {
        Book entity = new Book();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setAuthor(this.authorRepository.findById(dto.getAuthorId()).get());
        entity.setPublishYear(dto.getPublishYear());
        return entity;
    }
}
