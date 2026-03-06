package com.buci.DaniTanit.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name = "book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "publish_year")
    private Integer publishYear;

    @ManyToOne()
    @JoinColumn(name = "author_id")
    private Author author;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt = modifiedTimer();

    @Column(name = "created_at")
    private LocalDateTime createdAt = createTimer();


    @ManyToMany(fetch= FetchType.LAZY)
    @JoinTable(
            name = "book_genres",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> genres;

    private LocalDateTime createTimer() {
        if (this.createdAt == null) {
            return LocalDateTime.now();
        } else return this.createdAt;

    }

    private LocalDateTime modifiedTimer() {
        if (createdAt == null) {
            return null;
        } else {
            return LocalDateTime.now();
        }
    }
}