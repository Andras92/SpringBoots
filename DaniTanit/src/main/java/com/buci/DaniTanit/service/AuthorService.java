package com.buci.DaniTanit.service;

import com.buci.DaniTanit.converter.AuthorConverter;
import com.buci.DaniTanit.dao.AuthorRepository;
import com.buci.DaniTanit.dto.AuthorDto;
import com.buci.DaniTanit.entities.Author;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;
    private final AuthorConverter authorConverter;

    public List<AuthorDto> findALl() {
        List<Author> authors = authorRepository.findAll();
        return authors.stream().map((a) -> authorConverter.toDto(a)).collect(Collectors.toList());
    }

    public AuthorDto findById(Long id) {
        Optional<Author> author = authorRepository.findById(id);
        return authorConverter.toDto(author.get());
    }

    public Long create(AuthorDto author) {
        return authorRepository.save(authorConverter.toEntity(author)).getId();
    }
}
