package com.buci.DaniTanit.converter;

import com.buci.DaniTanit.dao.AuthorRepository;
import com.buci.DaniTanit.dao.GenreRepository;
import com.buci.DaniTanit.dto.BookDto;
import com.buci.DaniTanit.entities.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class BookConverter {
    private final AuthorRepository authorRepository;
    private final GenreRepository genreRepository;

    public BookDto toDto(Book entity) {
        BookDto dto = new BookDto();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setPublishYear(entity.getPublishYear());
        
        // Null check az author-nál
        if (entity.getAuthor() != null) {
            dto.setAuthorId(entity.getAuthor().getId());
        }
        
        // Null check és üres lista ellenőrzés a genre-knél
        if (entity.getGenres() != null && !entity.getGenres().isEmpty()) {
            dto.setGenreIds(entity.getGenres().stream()
                .map(genre -> genre.getId())
                .collect(Collectors.toList()));
        }
        
        return dto;
    }

    public Book toEntity(BookDto dto) {
        Book entity = new Book();
        entity.setId(dto.getId());
        entity.setName(dto.getName());
        entity.setPublishYear(dto.getPublishYear());
        
        // Author beállítása null check-kel és biztonságos Optional kezeléssel
        if (dto.getAuthorId() != null) {
            entity.setAuthor(
                authorRepository.findById(dto.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("Author not found with id: " + dto.getAuthorId()))
            );
        }
        
        // Genre-k beállítása null check-kel
        if (dto.getGenreIds() != null && !dto.getGenreIds().isEmpty()) {
            entity.setGenres(genreRepository.findAllById(dto.getGenreIds()));
        }
        
        return entity;
    }
}
