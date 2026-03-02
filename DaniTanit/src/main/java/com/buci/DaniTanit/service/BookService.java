package com.buci.DaniTanit.service;

import com.buci.DaniTanit.converter.BookConverter;
import com.buci.DaniTanit.dao.BookRepository;
import com.buci.DaniTanit.dto.BookDto;
import com.buci.DaniTanit.entities.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookConverter bookConverter;

    public List<BookDto> findAll() {
        List<Book> books = bookRepository.findAll();
        return books.stream().map((book) -> bookConverter.toDto(book)).collect(Collectors.toList());
    }

    public BookDto findById(Long id) {
        Optional<Book> book = bookRepository.findById(id);
        return this.bookConverter.toDto(book.get());
    }

    public Long create(BookDto book) {
        return bookRepository.save(bookConverter.toEntity(book)).getId();
    }

    public void delete(Long id) {

        bookRepository.deleteById(id);
    }

    public BookDto update(BookDto book) {
        Book modified = bookRepository.save(this.bookConverter.toEntity(book));
        return bookConverter.toDto(modified);
    }
}
