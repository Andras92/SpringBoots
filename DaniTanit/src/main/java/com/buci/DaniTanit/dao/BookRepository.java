package com.buci.DaniTanit.dao;

import com.buci.DaniTanit.entities.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    
    // 1. módszer: Spring Data JPA naming convention
    // Automatikusan generálja a query-t a metódus nevéből
    List<Book> findByAuthorNameStartingWith(String prefix);
    
    // 2. módszer: JPQL query @Query annotációval
    @Query("SELECT b FROM Book b WHERE b.author.name LIKE :prefix%")
    List<Book> findBooksByAuthorNameStartingWithJPQL(@Param("prefix") String prefix);
    
    // 3. módszer: Native SQL query
    @Query(value = "SELECT * FROM book b INNER JOIN author a ON b.author_id = a.id WHERE a.name LIKE :prefix%", nativeQuery = true)
    List<Book> findBooksByAuthorNameStartingWithNative(@Param("prefix") String prefix);
    
    // BÓNUSZ: Könyvek keresése ahol a szerző neve 'V' betűvel kezdődik (konkrét)
    @Query("SELECT b FROM Book b WHERE b.author.name LIKE 'V%'")
    List<Book> findBooksWithAuthorNameStartingWithV();
    
    // ========== ALAPVETŐ KERESÉSEK ==========
    
    // Könyvek egy adott év után publikálva
    List<Book> findByPublishYearGreaterThan(Integer year);
    
    // Könyvek egy adott év előtt publikálva
    List<Book> findByPublishYearLessThan(Integer year);
    
    // Könyvek egy adott év intervallumban
    List<Book> findByPublishYearBetween(Integer startYear, Integer endYear);
    
    // Könyv cím alapján (case insensitive)
    Optional<Book> findByNameIgnoreCase(String name);
    
    // Könyvek amelyek címe tartalmazza a keresett szöveget
    List<Book> findByNameContaining(String keyword);
    
    // Könyvek amelyek címe tartalmazza a szöveget (case insensitive)
    List<Book> findByNameContainingIgnoreCase(String keyword);
    
    // ========== RENDEZÉSEK ==========
    
    // Összes könyv publikálási év szerint rendezve (növekvő)
    List<Book> findAllByOrderByPublishYearAsc();
    
    // Összes könyv publikálási év szerint rendezve (csökkenő)
    List<Book> findAllByOrderByPublishYearDesc();
    
    // Összes könyv cím szerint ABC sorrendben
    List<Book> findAllByOrderByNameAsc();
    
    // Könyvek egy adott év után, publikálási év szerint rendezve
    List<Book> findByPublishYearGreaterThanOrderByPublishYearAsc(Integer year);
    
    // ========== SZERZŐ ALAPÚ KERESÉSEK ==========
    
    // Összes könyv egy adott szerzőtől
    List<Book> findByAuthorId(Long authorId);
    
    // Könyvek ahol a szerző neve tartalmaz egy szöveget
    List<Book> findByAuthorNameContaining(String nameFragment);
    
    // Könyvek ahol a szerző neve pontosan egyezik (case insensitive)
    List<Book> findByAuthorNameIgnoreCase(String authorName);
    
    // Könyvek adott szerző(k)től (IN query)
    List<Book> findByAuthorIdIn(List<Long> authorIds);
    
    // ========== MŰFAJ (GENRE) ALAPÚ KERESÉSEK ==========
    
    // Könyvek amelyek rendelkeznek legalább egy műfajjal
    @Query("SELECT DISTINCT b FROM Book b WHERE b.genres IS NOT EMPTY")
    List<Book> findBooksWithGenres();
    
    // Könyvek amelyeknek nincs műfajuk
    @Query("SELECT b FROM Book b WHERE b.genres IS EMPTY OR b.genres IS NULL")
    List<Book> findBooksWithoutGenres();
    
    // Könyvek egy adott műfajjal
    @Query("SELECT b FROM Book b JOIN b.genres g WHERE g.id = :genreId")
    List<Book> findByGenreId(@Param("genreId") Long genreId);
    
    // Könyvek amelyek rendelkeznek egy adott műfajjal (típus alapján)
    @Query("SELECT b FROM Book b JOIN b.genres g WHERE g.type = :genreType")
    List<Book> findByGenreType(@Param("genreType") String genreType);
    
    // ========== KOMBINÁLT FELTÉTELEK ==========
    
    // Könyvek adott szerzőtől és évtartományban
    List<Book> findByAuthorIdAndPublishYearBetween(Long authorId, Integer startYear, Integer endYear);
    
    // Könyvek cím vagy szerző neve alapján
    @Query("SELECT b FROM Book b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(b.author.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Book> searchByTitleOrAuthorName(@Param("keyword") String keyword);
    
    // Könyvek adott szerző nevével ÉS adott év után
    List<Book> findByAuthorNameAndPublishYearGreaterThan(String authorName, Integer year);
    
    // ========== AGGREGÁCIÓK & STATISZTIKÁK ==========
    
    // Könyvek száma egy adott szerzőtől
    @Query("SELECT COUNT(b) FROM Book b WHERE b.author.id = :authorId")
    Long countBooksByAuthorId(@Param("authorId") Long authorId);
    
    // Legrégebbi könyv publikálási éve
    @Query("SELECT MIN(b.publishYear) FROM Book b")
    Integer findOldestPublishYear();
    
    // Legújabb könyv publikálási éve
    @Query("SELECT MAX(b.publishYear) FROM Book b")
    Integer findNewestPublishYear();
    
    // Könyvek száma műfaj szerint csoportosítva
    @Query("SELECT g.type, COUNT(b) FROM Book b JOIN b.genres g GROUP BY g.type")
    List<Object[]> countBooksByGenre();
    
    // ========== LÉTEZÉS ELLENŐRZÉS ==========
    
    // Létezik-e könyv adott címmel
    boolean existsByName(String name);
    
    // Létezik-e könyv adott szerzőtől
    boolean existsByAuthorId(Long authorId);
    
    // Létezik-e könyv adott címmel és szerzőtől (duplikáció ellenőrzés)
    boolean existsByNameAndAuthorId(String name, Long authorId);
    
    // ========== TOP N & LIMIT ==========
    
    // Első N könyv publikálási év szerint (legrégebbiek)
    List<Book> findTop5ByOrderByPublishYearAsc();
    
    // Első N könyv publikálási év szerint (legújabbak)
    List<Book> findTop10ByOrderByPublishYearDesc();
    
    // Első N könyv egy adott szerzőtől (legújabbak)
    List<Book> findTop3ByAuthorIdOrderByPublishYearDesc(Long authorId);
    
    // ========== NULL KEZELÉS ==========
    
    // Könyvek amelyeknek nincs szerzője (árva könyvek)
    List<Book> findByAuthorIsNull();
    
    // Könyvek amelyeknek van szerzője
    List<Book> findByAuthorIsNotNull();
    
    // Könyvek amelyeknek nincs publikálási éve
    List<Book> findByPublishYearIsNull();
    
    // ========== TÖRLÉS QUERY-K ==========
    
    // Könyvek törlése egy adott év előtt
    @Query("DELETE FROM Book b WHERE b.publishYear < :year")
    void deleteByPublishYearBefore(@Param("year") Integer year);
    
    // Könyvek törlése adott szerzőtől
    void deleteByAuthorId(Long authorId);
    
    // ========== FRISSÍTÉS QUERY-K ==========
    
    // Könyv publikálási évének frissítése
    @Query("UPDATE Book b SET b.publishYear = :newYear WHERE b.id = :bookId")
    void updatePublishYear(@Param("bookId") Long bookId, @Param("newYear") Integer newYear);
    
    // Összes könyv publikálási évének növelése (teszt célra)
    @Query("UPDATE Book b SET b.publishYear = b.publishYear + :years WHERE b.author.id = :authorId")
    void incrementPublishYearByAuthor(@Param("authorId") Long authorId, @Param("years") Integer years);
    
    // ========== KOMPLEX KERESÉSEK ==========
    
    // Könyvek amelyek egy bizonyos szöveg alapján kereshetők (cím vagy szerző)
    @Query("SELECT b FROM Book b WHERE " +
           "LOWER(b.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(b.author.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "CAST(b.publishYear AS string) LIKE CONCAT('%', :searchTerm, '%')")
    List<Book> fullTextSearch(@Param("searchTerm") String searchTerm);
    
    // Könyvek adott műfajokkal (legalább egy egyezik)
    @Query("SELECT DISTINCT b FROM Book b JOIN b.genres g WHERE g.id IN :genreIds")
    List<Book> findByGenreIdIn(@Param("genreIds") List<Long> genreIds);
    
    // Könyvek amelyek pontosan egy adott műfajkombinációval rendelkeznek
    @Query("SELECT b FROM Book b WHERE SIZE(b.genres) = :genreCount")
    List<Book> findByGenreCount(@Param("genreCount") int genreCount);
    
    // Legtöbb könyvet író szerzők könyvei (top szerzők)
    @Query("SELECT b FROM Book b WHERE b.author.id IN " +
           "(SELECT b2.author.id FROM Book b2 GROUP BY b2.author.id HAVING COUNT(b2) > :minBooks)")
    List<Book> findBooksFromProlificAuthors(@Param("minBooks") Long minBooks);
    
    // ========== DÁTUM ALAPÚ QUERY-K (ha használnád) ==========
    
    // Könyvek amelyek az elmúlt N napban lettek létrehozva
    @Query("SELECT b FROM Book b WHERE b.createdAt >= :date")
    List<Book> findBooksCreatedAfter(@Param("date") java.time.LocalDateTime date);
    
    // Könyvek amelyek az elmúlt időszakban módosultak
    @Query("SELECT b FROM Book b WHERE b.modifiedAt >= :date")
    List<Book> findBooksModifiedAfter(@Param("date") java.time.LocalDateTime date);
    
    // Legutóbb létrehozott N könyv
    List<Book> findTop10ByOrderByCreatedAtDesc();
}
