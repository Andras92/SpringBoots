package com.buci.DaniTanit.presentation;

import com.buci.DaniTanit.dto.GenreDto;
import com.buci.DaniTanit.service.GenreService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/rest/genre", produces = MediaType.APPLICATION_JSON_VALUE)
public class GenreController {
    private final GenreService genreService;

    @GetMapping("/all")
    public ResponseEntity<List<GenreDto>> findAll(){
        List<GenreDto> genres = genreService.findAll();
        return new ResponseEntity<>(genres, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Long> create(@RequestBody GenreDto genre){
        return new ResponseEntity<>(genreService.create(genre), HttpStatus.CREATED);
    }
}
