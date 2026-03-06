package com.buci.DaniTanit.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Table(name="publish")
public class Publish {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "publish_date")
    private LocalDateTime publishDate;

    @OneToOne
    @JoinColumn(name = "book_id")
    private Book book;

    @OneToOne
    @JoinColumn(name = "author_id")
    private Author author;
    
    // Automatikusan beállítja a dátumot mentés előtt, ha nincs megadva
    @PrePersist
    protected void onCreate() {
        if (publishDate == null) {
            publishDate = LocalDateTime.now();
        }
    }
}
