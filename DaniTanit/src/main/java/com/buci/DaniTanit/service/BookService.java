package com.buci.DaniTanit.service;

import com.buci.DaniTanit.converter.BookConverter;
import com.buci.DaniTanit.dao.BookRepository;
import com.buci.DaniTanit.dto.BookDto;
import com.buci.DaniTanit.entities.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    private final BookConverter bookConverter;

    public List<BookDto> findAll() {
        List<Book> books = bookRepository.findAll();
        return books.stream()
            .map(bookConverter::toDto)
            .collect(Collectors.toList());
    }

    public BookDto findById(Long id) {
        Book book = bookRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
        return bookConverter.toDto(book);
    }

    public Long create(BookDto book) {
        return bookRepository.save(bookConverter.toEntity(book)).getId();
    }

    public void delete(Long id) {
        // Ellenőrizzük, hogy létezik-e a könyv törlés előtt
        if (!bookRepository.existsById(id)) {
            throw new RuntimeException("Book not found with id: " + id);
        }
        bookRepository.deleteById(id);
    }

    public BookDto update(BookDto bookDto) {
        // Ellenőrizzük, hogy létezik-e a könyv
        Book existingBook = bookRepository.findById(bookDto.getId())
            .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookDto.getId()));
        
        // Módosítjuk a meglévő könyv adatait (így megtartjuk a createdAt-ot)
        existingBook.setName(bookDto.getName());
        existingBook.setPublishYear(bookDto.getPublishYear());
        
        // Author frissítése
        if (bookDto.getAuthorId() != null) {
            existingBook.setAuthor(
                bookConverter.toEntity(bookDto).getAuthor()
            );
        }
        
        // Genre-k frissítése
        if (bookDto.getGenreIds() != null) {
            existingBook.setGenres(
                bookConverter.toEntity(bookDto).getGenres()
            );
        }
        
        Book savedBook = bookRepository.save(existingBook);
        return bookConverter.toDto(savedBook);
    }
    
    // Könyvek keresése szerző neve alapján (betűvel kezdődik)
    public List<BookDto> findBooksByAuthorNameStartingWith(String prefix) {
        List<Book> books = bookRepository.findByAuthorNameStartingWith(prefix);
        return books.stream()
            .map(bookConverter::toDto)
            .collect(Collectors.toList());
    }
    
    // Konkrétan 'V' betűvel kezdődő szerzők könyvei
    public List<BookDto> findBooksWithAuthorNameStartingWithV() {
        List<Book> books = bookRepository.findBooksWithAuthorNameStartingWithV();
        return books.stream()
            .map(bookConverter::toDto)
            .collect(Collectors.toList());
    }
}
