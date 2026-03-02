package com.buci.DaniTanit.presentation;

import com.buci.DaniTanit.dto.BookDto;
import com.buci.DaniTanit.entities.Book;
import com.buci.DaniTanit.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/rest/book", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class BookController {
    private final BookService bookService;

    @GetMapping("/all")
    public ResponseEntity<List<BookDto>> findAll() {
        List<BookDto> books = bookService.findAll();
        return new ResponseEntity<>(books, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<BookDto> findById(@PathVariable Long id) {
        BookDto book = bookService.findById(id);
        return new ResponseEntity<>(book, HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<Long> create(@RequestBody BookDto book) {
        return new ResponseEntity<>(bookService.create(book), HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Long> delete(@PathVariable Long id) {
        bookService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/update")
    public ResponseEntity<BookDto> update(@RequestBody BookDto book) {
        return new ResponseEntity<>(bookService.update(book), HttpStatus.OK);
    }
}
