package com.buci.DaniTanit.converter;

import com.buci.DaniTanit.dao.BookRepository;
import com.buci.DaniTanit.dao.GenreRepository;
import com.buci.DaniTanit.dto.GenreDto;
import com.buci.DaniTanit.entities.Genre;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class GenreConverter {
    private final BookRepository bookRepository;

    public GenreDto toDto(Genre genre){
        GenreDto dto = new GenreDto();
        dto.setId(genre.getId());
        dto.setType(genre.getType());
        dto.setBooks(genre.getBooks().size());
        return dto;
    }

    public Genre toEntity(GenreDto dto){
        Genre genre = new Genre();
        genre.setId(dto.getId());
        genre.setType(dto.getType());
        genre.setBooks(this.bookRepository.findAllById(Collections.singleton(dto.getId())));
        return genre;
    }
}
