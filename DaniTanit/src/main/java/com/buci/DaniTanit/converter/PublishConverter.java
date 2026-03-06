package com.buci.DaniTanit.converter;

import com.buci.DaniTanit.dao.AuthorRepository;
import com.buci.DaniTanit.dao.BookRepository;
import com.buci.DaniTanit.dto.PublishDto;
import com.buci.DaniTanit.entities.Publish;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class PublishConverter {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public PublishDto toDto(Publish entity) {
        PublishDto dto = new PublishDto();
        dto.setId(entity.getId());
        dto.setPublishDate(entity.getPublishDate());

        if (entity.getBook() != null) {
            dto.setBookId(entity.getBook().getId());
        }

        if (entity.getAuthor() != null) {
            dto.setAuthorId(entity.getAuthor().getId());
        }
        
        return dto;
    }

    public Publish toEntity(PublishDto dto) {
        Publish entity = new Publish();
        entity.setId(dto.getId());
        entity.setPublishDate(dto.getPublishDate());

        if (dto.getBookId() != null) {
            entity.setBook(
                bookRepository.findById(dto.getBookId())
                    .orElseThrow(() -> new RuntimeException("Book not found with id: " + dto.getBookId()))
            );
        } else {
            throw new RuntimeException("Book ID is required");
        }

        if (dto.getAuthorId() != null) {
            entity.setAuthor(
                authorRepository.findById(dto.getAuthorId())
                    .orElseThrow(() -> new RuntimeException("Author not found with id: " + dto.getAuthorId()))
            );
        } else {
            throw new RuntimeException("Author ID is required");
        }
        
        return entity;
    }
}
