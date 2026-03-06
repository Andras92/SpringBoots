# 🔧 Változtatások Összefoglalása

## 📋 Áttekintés

Javítottam a kódban a kritikus hibákat és implementáltam a Publish API-t teljes DTO pattern-nel és automatikus dátum kezeléssel.

---

## 1️⃣ BookConverter.java - Kritikus null pointer javítások

### ❌ Előtte (VESZÉLYES):
```java
public BookDto toDto(Book entity) {
    dto.setAuthorId(entity.getAuthor().getId());  // NullPointerException!
    dto.setGenreIds(entity.getGenres().stream()...);  // NullPointerException!
}

public Book toEntity(BookDto dto) {
    entity.setAuthor(authorRepository.findById(dto.getAuthorId()).get());  // NoSuchElementException!
    entity.setGenres(genreRepository.findAllById(dto.getGenreIds()));  // NullPointerException!
}
```

### ✅ Utána (BIZTONSÁGOS):
```java
public BookDto toDto(Book entity) {
    // Null check az author-nál
    if (entity.getAuthor() != null) {
        dto.setAuthorId(entity.getAuthor().getId());
    }
    
    // Null check és üres lista ellenőrzés a genre-knél
    if (entity.getGenres() != null && !entity.getGenres().isEmpty()) {
        dto.setGenreIds(entity.getGenres().stream()
            .map(genre -> genre.getId())
            .collect(Collectors.toList()));
    }
}

public Book toEntity(BookDto dto) {
    // Biztonságos Optional kezelés
    if (dto.getAuthorId() != null) {
        entity.setAuthor(
            authorRepository.findById(dto.getAuthorId())
                .orElseThrow(() -> new RuntimeException("Author not found with id: " + dto.getAuthorId()))
        );
    }
    
    // Null check a genre-knél
    if (dto.getGenreIds() != null && !dto.getGenreIds().isEmpty()) {
        entity.setGenres(genreRepository.findAllById(dto.getGenreIds()));
    }
}
```

### 💡 Magyarázat:
- **Null check-ek:** Ellenőrizzük, hogy a field-ek nem null-ak
- **`.orElseThrow()`:** Biztonságosan kezeljük az Optional-t, értelmes hibaüzenettel
- **Lambda javítás:** `(g)->g.getId()` helyett `genre -> genre.getId()` (olvashatóbb)

---

## 2️⃣ BookService.java - Unsafe .get() eltávolítása

### ❌ Előtte:
```java
public BookDto findById(Long id) {
    Optional<Book> book = bookRepository.findById(id);
    return this.bookConverter.toDto(book.get());  // NoSuchElementException!
}

public BookDto update(BookDto book) {
    Book modified = bookRepository.save(this.bookConverter.toEntity(book));
    return bookConverter.toDto(modified);
    // Elveszíti a createdAt timestamp-et!
}
```

### ✅ Utána:
```java
public BookDto findById(Long id) {
    Book book = bookRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));
    return bookConverter.toDto(book);
}

public BookDto update(BookDto bookDto) {
    // Lekérjük a meglévő könyvet
    Book existingBook = bookRepository.findById(bookDto.getId())
        .orElseThrow(() -> new RuntimeException("Book not found with id: " + bookDto.getId()));
    
    // Módosítjuk a field-jeit (így megtartjuk a createdAt-ot)
    existingBook.setName(bookDto.getName());
    existingBook.setPublishYear(bookDto.getPublishYear());
    
    // Author és Genre-k frissítése
    if (bookDto.getAuthorId() != null) {
        existingBook.setAuthor(bookConverter.toEntity(bookDto).getAuthor());
    }
    if (bookDto.getGenreIds() != null) {
        existingBook.setGenres(bookConverter.toEntity(bookDto).getGenres());
    }
    
    Book savedBook = bookRepository.save(existingBook);
    return bookConverter.toDto(savedBook);
}
```

### 💡 Magyarázat:
- **findById:** `.get()` helyett `.orElseThrow()` értelmes hibaüzenettel
- **update:** Meglévő entitást módosítunk, nem új-at hozunk létre → **megőrzi a createdAt dátumot**
- **delete:** Ellenőrizzük, hogy létezik-e a könyv törlés előtt
- **Lambda:** Method reference használata (`bookConverter::toDto`)

---

## 3️⃣ Publish.java - Automatikus dátum beállítás

### ❌ Előtte:
```java
@Column
private Date date;  // Nem LocalDateTime, nincs automatizmus

@OneToOne
@JoinColumn  // Nincs megadva az oszlop név
private Book book;
```

### ✅ Utána:
```java
@Column(name = "publish_date")
private LocalDateTime publishDate;

@OneToOne
@JoinColumn(name = "book_id")
private Book book;

@OneToOne
@JoinColumn(name = "author_id")
private Author author;

// Automatikus dátum beállítás mentés előtt
@PrePersist
protected void onCreate() {
    if (publishDate == null) {
        publishDate = LocalDateTime.now();
    }
}
```

### 💡 Magyarázat:
- **Date → LocalDateTime:** Modern Java dátumkezelés
- **@PrePersist:** JPA callback - automatikusan lefut mentés előtt
- **Logika:** Ha nem adsz meg dátumot, automatikusan beállítja a jelenlegi időt
- **Explicit oszlopnevek:** `@JoinColumn(name = "book_id")` - tisztább SQL

---

## 4️⃣ DTO Pattern implementálása Publish-hoz

### Új fájlok:

#### A) PublishDto.java
```java
@Data
public class PublishDto {
    private Long id;
    private Long bookId;          // Nem a teljes Book objektum
    private Long authorId;        // Nem a teljes Author objektum
    private LocalDateTime publishDate;  // Opcionális
}
```

**Miért jó?**
- Nem exposed-oljuk az entitásokat a Controller-en keresztül
- Könnyebb JSON serialization/deserialization
- Elkerüljük a circular reference problémákat

---

#### B) PublishConverter.java
```java
@Component
@RequiredArgsConstructor
public class PublishConverter {
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public PublishDto toDto(Publish entity) {
        // Entity → DTO konverzió null check-kel
    }

    public Publish toEntity(PublishDto dto) {
        // DTO → Entity konverzió validációval
        // Ellenőrzi, hogy léteznek-e a hivatkozott entitások
    }
}
```

**Miért fontos?**
- Elválasztja az adatbázis modellt a REST API-tól
- Validálja, hogy léteznek-e az ID-k
- Értelmes hibaüzeneteket ad

---

## 5️⃣ PublishService.java - Teljes újraírás

### ❌ Előtte:
```java
public List<Publish> findAll() {
    return publishRepository.findAll();  // Entity-t ad vissza!
}

public Long create(Publish publish) {
    return publishRepository.save(publish).getId();  // Entity-t fogad!
}
```

### ✅ Utána:
```java
public List<PublishDto> findAll() {
    return publishRepository.findAll().stream()
        .map(publishConverter::toDto)
        .collect(Collectors.toList());
}

public PublishDto findById(Long id) {
    Publish publish = publishRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Publish not found with id: " + id));
    return publishConverter.toDto(publish);
}

public Long create(PublishDto publishDto) {
    Publish publish = publishConverter.toEntity(publishDto);
    return publishRepository.save(publish).getId();
}

public void delete(Long id) {
    if (!publishRepository.existsById(id)) {
        throw new RuntimeException("Publish not found with id: " + id);
    }
    publishRepository.deleteById(id);
}
```

### 💡 Magyarázat:
- **DTO-k mindenhol:** Service nem entitásokkal dolgozik kifelé
- **Biztonságos kezelés:** `.orElseThrow()` használata
- **Új metódusok:** `findById()` és `delete()`

---

## 6️⃣ PublishController.java - REST API javítások

### ❌ Előtte:
```java
@GetMapping("/all")
public ResponseEntity<List<Publish>> findAll() {
    List<Publish> publishes = publishService.findAll();
    return new ResponseEntity<>(publishes, HttpStatus.OK);
}

@PostMapping("/create")
public ResponseEntity<Long> create(@RequestBody Publish publish) {
    return new ResponseEntity<>(publishService.create(publish), HttpStatus.CREATED);
}
```

### ✅ Utána:
```java
@RestController
@RequestMapping(value = "/rest/publish", produces = MediaType.APPLICATION_JSON_VALUE)
public class PublishController {

    @GetMapping("/all")
    public ResponseEntity<List<PublishDto>> findAll() {
        return ResponseEntity.ok(publishService.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PublishDto> findById(@PathVariable Long id) {
        return ResponseEntity.ok(publishService.findById(id));
    }

    @PostMapping("/create")
    public ResponseEntity<Long> create(@RequestBody PublishDto publishDto) {
        return new ResponseEntity<>(publishService.create(publishDto), HttpStatus.CREATED);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        publishService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

### 💡 Magyarázat:
- **DTO-k:** PublishDto használata Publish helyett
- **Új endpoint:** `GET /{id}` - egyedi publikálás lekérdezése
- **DELETE endpoint:** Törlés támogatás
- **ResponseEntity.ok():** Rövidebb szintaxis
- **produces:** Explicit JSON content type
- **NO_CONTENT:** DELETE-nél 204 státusz (REST best practice)

---

## 📊 Változtatások összesítése

| Fájl | Változtatás típusa | Miért fontos? |
|------|-------------------|---------------|
| BookConverter.java | 🔴 Kritikus javítás | Null pointer kivételek elkerülése |
| BookService.java | 🔴 Kritikus javítás | Unsafe .get() eltávolítása |
| Publish.java | 🟡 Logikai javítás | Automatikus dátum beállítás |
| PublishDto.java | 🟢 Új fájl | DTO pattern |
| PublishConverter.java | 🟢 Új fájl | Entity-DTO konverzió |
| PublishService.java | 🟡 Teljes újraírás | DTO pattern + biztonság |
| PublishController.java | 🟡 Javítás | REST best practices |

---

## 🎯 Főbb tanulságok

### 1. **Null Check Mindenütt**
```java
if (entity.getAuthor() != null) {
    // Csak akkor használjuk
}
```

### 2. **Optional Biztonságosan**
```java
.orElseThrow(() -> new RuntimeException("Not found"))  // ✅ Jó
.get()  // ❌ Veszélyes
```

### 3. **DTO Pattern**
```
Controller → DTO → Service → Converter → Entity → Repository
```

### 4. **JPA Callbacks**
```java
@PrePersist  // Mentés előtt
@PreUpdate   // Frissítés előtt
```

### 5. **REST Konvenciók**
- GET → 200 OK
- POST → 201 CREATED
- DELETE → 204 NO CONTENT
- Error → 404 NOT FOUND / 500 INTERNAL SERVER ERROR

---

## ✅ Következő lépések (opcionális)

1. **Custom Exception osztályok** (`BookNotFoundException`, `PublishNotFoundException`)
2. **@ControllerAdvice** - globális exception kezelés
3. **Validáció** - `@Valid`, `@NotNull`, `@NotBlank` a DTO-kban
4. **@Data lecserélése** bidirectional kapcsolatoknál (`@Getter`, `@Setter`, `@ToString(exclude=...)`)
5. **Integration tesztek** - Postman collection

---

🎉 **A kód mostmár biztonságos és működik automatikus dátum beállítással!**
