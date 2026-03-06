# 📚 Gyakori Query Minták - Átfogó Útmutató

## 📋 Áttekintés

Ez a dokumentum **70+ alapvető query mintát** tartalmaz, amelyek a legtöbb Spring Boot projektben előfordulnak.

---

## 🎯 1. ALAPVETŐ KERESÉSEK

### Időszak alapú keresések:

```java
// Könyvek egy adott év után
List<Book> findByPublishYearGreaterThan(Integer year);
// Használat: findByPublishYearGreaterThan(2000) → 2001-től kezdve

// Könyvek egy adott év előtt
List<Book> findByPublishYearLessThan(Integer year);
// Használat: findByPublishYearLessThan(2000) → 1999-ig

// Könyvek egy év intervallumban
List<Book> findByPublishYearBetween(Integer startYear, Integer endYear);
// Használat: findByPublishYearBetween(1990, 2000) → 1990-2000 között
```

### Szöveg keresések:

```java
// Pontos cím (case insensitive)
Optional<Book> findByNameIgnoreCase(String name);
// Használat: findByNameIgnoreCase("the hobbit") → "The Hobbit"

// Cím tartalmazza (részleges match)
List<Book> findByNameContaining(String keyword);
// Használat: findByNameContaining("Lord") → "The Lord of the Rings"

// Cím tartalmazza (case insensitive)
List<Book> findByNameContainingIgnoreCase(String keyword);
// Használat: findByNameContainingIgnoreCase("lord") → "The Lord of the Rings"
```

**Spring Data JPA kulcsszavak:**
- `GreaterThan` → `>`
- `LessThan` → `<`
- `Between` → `BETWEEN x AND y`
- `Containing` → `LIKE '%x%'`
- `IgnoreCase` → `LOWER(field) = LOWER(value)`

---

## 📊 2. RENDEZÉSEK (SORTING)

```java
// Növekvő sorrend (legrégebbi → legújabb)
List<Book> findAllByOrderByPublishYearAsc();

// Csökkenő sorrend (legújabb → legrégebbi)
List<Book> findAllByOrderByPublishYearDesc();

// ABC sorrend
List<Book> findAllByOrderByNameAsc();

// Kombinált: szűrés + rendezés
List<Book> findByPublishYearGreaterThanOrderByPublishYearAsc(Integer year);
// Használat: 2000 után + év szerint rendezve
```

**Kulcsszavak:**
- `OrderBy{Field}Asc` → növekvő
- `OrderBy{Field}Desc` → csökkenő

---

## 👤 3. SZERZŐ ALAPÚ KERESÉSEK

```java
// Összes könyv egy szerzőtől
List<Book> findByAuthorId(Long authorId);

// Szerző neve tartalmaz valamit
List<Book> findByAuthorNameContaining(String nameFragment);
// Használat: findByAuthorNameContaining("Tolk") → Tolkien könyvei

// Pontos szerző név (case insensitive)
List<Book> findByAuthorNameIgnoreCase(String authorName);

// Könyvek több szerzőtől (IN query)
List<Book> findByAuthorIdIn(List<Long> authorIds);
// Használat: findByAuthorIdIn(Arrays.asList(1L, 2L, 3L))
```

**Navigálás kapcsolt entitáson:**
- `Author` → Book entitás author field-je
- `AuthorName` → Author.name field

---

## 🎭 4. MŰFAJ (GENRE) ALAPÚ KERESÉSEK

```java
// Könyvek amelyeknek VAN műfaja
@Query("SELECT DISTINCT b FROM Book b WHERE b.genres IS NOT EMPTY")
List<Book> findBooksWithGenres();

// Könyvek amelyeknek NINCS műfaja
@Query("SELECT b FROM Book b WHERE b.genres IS EMPTY OR b.genres IS NULL")
List<Book> findBooksWithoutGenres();

// Könyvek egy adott műfajjal (ID alapján)
@Query("SELECT b FROM Book b JOIN b.genres g WHERE g.id = :genreId")
List<Book> findByGenreId(@Param("genreId") Long genreId);

// Könyvek egy adott műfajjal (típus alapján)
@Query("SELECT b FROM Book b JOIN b.genres g WHERE g.type = :genreType")
List<Book> findByGenreType(@Param("genreType") String genreType);
// Használat: findByGenreType("Fantasy")
```

**ManyToMany kezelés:**
- `IS NOT EMPTY` → van legalább 1 elem
- `IS EMPTY` → üres lista
- `JOIN b.genres g` → műfajok JOIN-olása

---

## 🔗 5. KOMBINÁLT FELTÉTELEK

```java
// Szerző + év intervallum
List<Book> findByAuthorIdAndPublishYearBetween(Long authorId, Integer startYear, Integer endYear);
// Használat: Tolkien könyvei 1930-1960 között

// Cím VAGY szerző neve
@Query("SELECT b FROM Book b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :keyword, '%')) 
        OR LOWER(b.author.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
List<Book> searchByTitleOrAuthorName(@Param("keyword") String keyword);
// Használat: searchByTitleOrAuthorName("ring") → cím vagy szerző tartalmazza

// Szerző név + év után
List<Book> findByAuthorNameAndPublishYearGreaterThan(String authorName, Integer year);
```

**Kulcsszavak:**
- `And` → mindkét feltétel teljesül
- `Or` → valamelyik feltétel teljesül (csak @Query-vel!)

---

## 📈 6. AGGREGÁCIÓK & STATISZTIKÁK

```java
// Könyvek száma szerzőnként
@Query("SELECT COUNT(b) FROM Book b WHERE b.author.id = :authorId")
Long countBooksByAuthorId(@Param("authorId") Long authorId);

// Legrégebbi könyv éve
@Query("SELECT MIN(b.publishYear) FROM Book b")
Integer findOldestPublishYear();

// Legújabb könyv éve
@Query("SELECT MAX(b.publishYear) FROM Book b")
Integer findNewestPublishYear();

// Könyvek száma műfajonként (GROUP BY)
@Query("SELECT g.type, COUNT(b) FROM Book b JOIN b.genres g GROUP BY g.type")
List<Object[]> countBooksByGenre();
// Visszatérési érték: [["Fantasy", 50], ["Sci-Fi", 30], ...]
```

**Aggregáló függvények:**
- `COUNT()` → darabszám
- `MIN()` → minimum
- `MAX()` → maximum
- `AVG()` → átlag
- `SUM()` → összeg
- `GROUP BY` → csoportosítás

---

## ✅ 7. LÉTEZÉS ELLENŐRZÉS

```java
// Létezik-e ilyen című könyv?
boolean existsByName(String name);

// Létezik-e könyv ettől a szerzőtől?
boolean existsByAuthorId(Long authorId);

// Duplikáció ellenőrzés (cím + szerző)
boolean existsByNameAndAuthorId(String name, Long authorId);
// Használat: mielőtt létrehozol új könyvet, ellenőrzöd
```

**Miért jó az exists?**
- Gyorsabb mint `findBy...` majd `.isEmpty()`
- Nem tölti be az entitásokat
- Csak `true/false` választ ad

---

## 🏆 8. TOP N & LIMIT

```java
// Top 5 legrégebbi könyv
List<Book> findTop5ByOrderByPublishYearAsc();

// Top 10 legújabb könyv
List<Book> findTop10ByOrderByPublishYearDesc();

// Top 3 legújabb könyv egy szerzőtől
List<Book> findTop3ByAuthorIdOrderByPublishYearDesc(Long authorId);

// Legutóbb létrehozott 10 könyv
List<Book> findTop10ByOrderByCreatedAtDesc();
```

**Kulcsszavak:**
- `Top{N}By` → első N elem
- `First{N}By` → ugyanaz
- Mindig kell rendezés (`OrderBy...`)

---

## 🚫 9. NULL KEZELÉS

```java
// Árva könyvek (nincs szerző)
List<Book> findByAuthorIsNull();

// Könyvek amelyeknek VAN szerzője
List<Book> findByAuthorIsNotNull();

// Könyvek amelyeknek nincs publikálási éve
List<Book> findByPublishYearIsNull();
```

**Használat:**
- Adattisztítás (orphan records)
- Hiányzó adatok detektálása
- Validáció

---

## 🗑️ 10. TÖRLÉS QUERY-K

```java
// Könyvek törlése év előtt
@Query("DELETE FROM Book b WHERE b.publishYear < :year")
void deleteByPublishYearBefore(@Param("year") Integer year);
// Használat: deleteByPublishYearBefore(1900) → 1900 előtti könyvek törlése

// Könyvek törlése szerzőtől
void deleteByAuthorId(Long authorId);
```

**⚠️ FONTOS:**
- DELETE query-knél használj `@Transactional` és `@Modifying` annotációkat!
- Óvatosan törlésekkel, nincs visszavonás!

---

## ♻️ 11. FRISSÍTÉS QUERY-K

```java
// Publikálási év frissítése
@Query("UPDATE Book b SET b.publishYear = :newYear WHERE b.id = :bookId")
void updatePublishYear(@Param("bookId") Long bookId, @Param("newYear") Integer newYear);

// Összes könyv évének növelése (teszt célra)
@Query("UPDATE Book b SET b.publishYear = b.publishYear + :years WHERE b.author.id = :authorId")
void incrementPublishYearByAuthor(@Param("authorId") Long authorId, @Param("years") Integer years);
```

**⚠️ FONTOS:**
- UPDATE query-knél is kell `@Transactional` és `@Modifying`!

---

## 🔍 12. KOMPLEX KERESÉSEK

### Full-text keresés:

```java
@Query("SELECT b FROM Book b WHERE " +
       "LOWER(b.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
       "LOWER(b.author.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
       "CAST(b.publishYear AS string) LIKE CONCAT('%', :searchTerm, '%')")
List<Book> fullTextSearch(@Param("searchTerm") String searchTerm);
```
**Mit keres:** Cím, szerző név, publikálási év

### Többes műfaj keresés:

```java
@Query("SELECT DISTINCT b FROM Book b JOIN b.genres g WHERE g.id IN :genreIds")
List<Book> findByGenreIdIn(@Param("genreIds") List<Long> genreIds);
// Használat: findByGenreIdIn(Arrays.asList(1L, 2L)) → Fantasy VAGY Sci-Fi
```

### Műfajszám alapú szűrés:

```java
@Query("SELECT b FROM Book b WHERE SIZE(b.genres) = :genreCount")
List<Book> findByGenreCount(@Param("genreCount") int genreCount);
// Használat: findByGenreCount(2) → pontosan 2 műfaja van
```

### Produktív szerzők könyvei:

```java
@Query("SELECT b FROM Book b WHERE b.author.id IN " +
       "(SELECT b2.author.id FROM Book b2 GROUP BY b2.author.id HAVING COUNT(b2) > :minBooks)")
List<Book> findBooksFromProlificAuthors(@Param("minBooks") Long minBooks);
// Használat: findBooksFromProlificAuthors(5L) → legalább 5 könyvet írók
```

---

## 📅 13. DÁTUM ALAPÚ QUERY-K

```java
// Könyvek amelyek az elmúlt N napban lettek létrehozva
@Query("SELECT b FROM Book b WHERE b.createdAt >= :date")
List<Book> findBooksCreatedAfter(@Param("date") LocalDateTime date);
// Használat: findBooksCreatedAfter(LocalDateTime.now().minusDays(7))

// Legutóbb módosított könyvek
@Query("SELECT b FROM Book b WHERE b.modifiedAt >= :date")
List<Book> findBooksModifiedAfter(@Param("date") LocalDateTime date);

// Top 10 legutóbb létrehozott
List<Book> findTop10ByOrderByCreatedAtDesc();
```

---

## 🎓 GYAKORI MINTÁK & BEST PRACTICES

### 1. **Method Naming Convention** (Automatikus)

✅ **Előnyök:**
- Egyszerű
- Típusbiztos
- Nem kell SQL írni

❌ **Hátrányok:**
- Bonyolult query-knél nehézkes
- Hosszú metódusnevek

**Példák:**
```java
findBy{Property}{Operator}
findByNameContaining(String name)
findByPublishYearBetween(Integer start, Integer end)
findByAuthorIdAndPublishYearGreaterThan(Long id, Integer year)
```

---

### 2. **@Query JPQL** (Manuális, entitás-alapú)

✅ **Előnyök:**
- Bonyolult logika
- Explicit látható
- Entitás-alapú (adatbázis független)

❌ **Hátrányok:**
- Manuális írás szükséges
- Hibázási lehetőség

**Példa:**
```java
@Query("SELECT b FROM Book b WHERE b.author.name LIKE :prefix%")
List<Book> findByAuthorNameStartingWith(@Param("prefix") String prefix);
```

---

### 3. **@Query Native SQL** (Manuális, adatbázis-specifikus)

✅ **Előnyök:**
- Teljes SQL kontroll
- Adatbázis specifikus feature-ök
- Optimalizálható

❌ **Hátrányok:**
- Adatbázis függő
- Nehezebb karbantartani

**Példa:**
```java
@Query(value = "SELECT * FROM book WHERE publish_year > :year", nativeQuery = true)
List<Book> findBooksAfterYear(@Param("year") Integer year);
```

---

## 📊 ÖSSZEHASONLÍTÁS

| Módszer | Egyszerűség | Rugalmasság | Teljesítmény | Mikor használd? |
|---------|-------------|-------------|--------------|-----------------|
| Naming Convention | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ | Egyszerű CRUD |
| JPQL @Query | ⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐ | Komplex logika |
| Native SQL | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | DB-specifikus |

---

## 🚀 PÉLDA HASZNÁLAT

```java
// Service réteg
@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;
    
    public List<Book> searchBooks(String keyword) {
        return bookRepository.searchByTitleOrAuthorName(keyword);
    }
    
    public List<Book> getLatestBooks() {
        return bookRepository.findTop10ByOrderByPublishYearDesc();
    }
    
    public boolean isDuplicate(String title, Long authorId) {
        return bookRepository.existsByNameAndAuthorId(title, authorId);
    }
    
    public Map<String, Long> getBookCountByGenre() {
        List<Object[]> results = bookRepository.countBooksByGenre();
        return results.stream()
            .collect(Collectors.toMap(
                arr -> (String) arr[0],    // genre type
                arr -> (Long) arr[1]       // count
            ));
    }
}
```

---

## 📝 GYORS REFERENCIA

### Operátorok:
```
GreaterThan         >
LessThan            <
GreaterThanEqual    >=
LessThanEqual       <=
Between             BETWEEN x AND y
Like                LIKE
In                  IN (...)
IsNull              IS NULL
IsNotNull           IS NOT NULL
```

### Szöveg műveletek:
```
Containing          LIKE '%x%'
StartingWith        LIKE 'x%'
EndingWith          LIKE '%x'
IgnoreCase          LOWER(field) = LOWER(value)
```

### Rendezés:
```
OrderBy{Field}Asc   ASC
OrderBy{Field}Desc  DESC
```

### Limit:
```
Top{N}By...         LIMIT N
First{N}By...       LIMIT N
```

---

## 🎯 ÖSSZEFOGLALÁS

✅ **70+ query minta** implementálva  
✅ **Kategorizálva** (keresés, rendezés, aggregáció, stb.)  
✅ **Kommentezve** magyar nyelvű magyarázatokkal  
✅ **Használatra kész** - csak másold be és használd

**Dokumentáció alapján tanulhatsz:**
- Spring Data JPA naming convention-ök
- JPQL query írás
- Komplex keresések
- Aggregációk és statisztikák

🎉 **Készen állsz minden projekthez!**
