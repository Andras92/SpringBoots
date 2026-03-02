package com.buci.DaniTanit.presentation;

import com.buci.DaniTanit.dto.AuthorDto;
import com.buci.DaniTanit.entities.Author;
import com.buci.DaniTanit.service.AuthorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/rest/author", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthorController {
    private final AuthorService authorService;

    @GetMapping("/all")
    public ResponseEntity<List<AuthorDto>> findAll() {
        List<AuthorDto> authors = authorService.findALl();
        return new ResponseEntity<>(authors, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AuthorDto> findById(@PathVariable Long id) {
        AuthorDto author = authorService.findById(id);
        return new ResponseEntity<>(author, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Long> create(@RequestBody AuthorDto author) {
        return new ResponseEntity<>(authorService.create(author), HttpStatus.OK);
    }
}
