package com.buci.DaniTanit.service;

import com.buci.DaniTanit.converter.GenreConverter;
import com.buci.DaniTanit.dao.GenreRepository;
import com.buci.DaniTanit.dto.GenreDto;
import com.buci.DaniTanit.entities.Genre;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GenreService {
    private final GenreRepository genreRepository;
    private final GenreConverter genreConverter;

    public List<GenreDto> findAll(){
        List<Genre> genres =  genreRepository.findAll();
        return genres.stream().map((g)->genreConverter.toDto(g)).collect(Collectors.toList());
    }

    public Long create(GenreDto genreDto){
        return genreRepository.save(genreConverter.toEntity(genreDto)).getId();
    }
}
