# 🔍 Könyvek keresése szerző neve alapján

## 📋 Áttekintés

Implementáltam **3 különböző módszert** a könyvek keresésére a szerző neve alapján, amikor a név egy megadott betűvel kezdődik.

---

## 🎯 1. Spring Data JPA Naming Convention

### BookRepository.java:
```java
List<Book> findByAuthorNameStartingWith(String prefix);
```

### Magyarázat:
- **Automatikus query generálás** - Spring Data JPA a metódus nevéből generálja az SQL-t
- `findBy` - keresés
- `Author` - Book entitás author field-je
- `Name` - Author entitás name field-je
- `StartingWith` - LIKE 'prefix%'

### Generált SQL:
```sql
SELECT * FROM book b 
INNER JOIN author a ON b.author_id = a.id 
WHERE a.name LIKE 'V%'
```

**Előnyök:**
✅ Egyszerű, tiszta kód  
✅ Típusbiztos  
✅ Nem kell SQL-t írni

---

## 🎯 2. JPQL Query (@Query annotáció)

### BookRepository.java:
```java
@Query("SELECT b FROM Book b WHERE b.author.name LIKE :prefix%")
List<Book> findBooksByAuthorNameStartingWithJPQL(@Param("prefix") String prefix);
```

### Magyarázat:
- **JPQL** (Java Persistence Query Language) - entitásokkal dolgozik
- `Book b` - Book entitás alias
- `b.author.name` - navigálás a kapcsolt entitáson keresztül
- `:prefix%` - named parameter

**Előnyök:**
✅ Explicit query látható  
✅ Bonyolultabb logikához  
✅ Entitás alapú (nem táblák)

---

## 🎯 3. Native SQL Query

### BookRepository.java:
```java
@Query(value = "SELECT * FROM book b INNER JOIN author a ON b.author_id = a.id WHERE a.name LIKE :prefix%", nativeQuery = true)
List<Book> findBooksByAuthorNameStartingWithNative(@Param("prefix") String prefix);
```

### Magyarázat:
- **Native SQL** - közvetlen SQL lekérdezés
- `nativeQuery = true` - nem JPQL, hanem SQL
- Táblákkal dolgozik (book, author), nem entitásokkal

**Előnyök:**
✅ Teljes SQL kontroll  
✅ Adatbázis specifikus feature-ök  
✅ Optimalizálható

**Hátrányok:**
⚠️ Adatbázis függő  
⚠️ Nehezebb karbantartani

---

## 🚀 REST API Használat

### 1. Bármilyen betűvel kezdődő szerzők keresése (paraméterrel)

**Endpoint:**
```
GET http://localhost:8080/rest/book/search/by-author?prefix=V
```

**Query Parameter:**
- `prefix` - A kezdőbetű(k) amit keresel

**Példák:**
```bash
# Szerzők akik 'V' betűvel kezdődnek
GET /rest/book/search/by-author?prefix=V

# Szerzők akik 'J' betűvel kezdődnek
GET /rest/book/search/by-author?prefix=J

# Szerzők akik 'Tol' kezdetűek (pl. Tolkien)
GET /rest/book/search/by-author?prefix=Tol
```

**Response példa:**
```json
[
  {
    "id": 1,
    "name": "The Hobbit",
    "publishYear": 1937,
    "authorId": 1,
    "genreIds": [1, 2]
  },
  {
    "id": 2,
    "name": "The Lord of the Rings",
    "publishYear": 1954,
    "authorId": 1,
    "genreIds": [1]
  }
]
```

---

### 2. Konkrétan 'V' betűvel kezdődő szerzők (fix)

**Endpoint:**
```
GET http://localhost:8080/rest/book/search/author-starts-with-v
```

**Nincs paraméter** - mindig 'V' betűt keres

**Példa:**
```bash
GET /rest/book/search/author-starts-with-v
```

**Response:** Ugyanaz mint fent, de csak 'V'-vel kezdődő szerzők könyvei

---

## 🧪 Postman Tesztelés

### Előkészítés - Hozz létre tesztadatokat:

#### 1. Hozz létre szerzőket 'V' betűvel:
```http
POST /rest/author/create

{
  "name": "Viktor Hugo"
}
```

```http
POST /rest/author/create

{
  "name": "Virginia Woolf"
}
```

```http
POST /rest/author/create

{
  "name": "J.R.R. Tolkien"
}
```

---

#### 2. Hozz létre könyveket:
```http
POST /rest/book/create

{
  "name": "Les Misérables",
  "publishYear": 1862,
  "authorId": 1,
  "genreIds": []
}
```

```http
POST /rest/book/create

{
  "name": "Mrs Dalloway",
  "publishYear": 1925,
  "authorId": 2,
  "genreIds": []
}
```

```http
POST /rest/book/create

{
  "name": "The Hobbit",
  "publishYear": 1937,
  "authorId": 3,
  "genreIds": []
}
```

---

#### 3. Keress 'V' betűvel kezdődő szerzők könyveit:
```http
GET /rest/book/search/by-author?prefix=V
```

**Várható eredmény:** 2 könyv (Viktor Hugo és Virginia Woolf könyvei)

---

#### 4. Keress 'J' betűvel kezdődő szerzők könyveit:
```http
GET /rest/book/search/by-author?prefix=J
```

**Várható eredmény:** 1 könyv (Tolkien könyve)

---

#### 5. Konkrét endpoint 'V' betűre:
```http
GET /rest/book/search/author-starts-with-v
```

**Várható eredmény:** 2 könyv (ugyanaz mint a 3. lépésben)

---

## 📊 Módszerek összehasonlítása

| Módszer | Egyszerűség | Rugalmasság | Teljesítmény | Ajánlott? |
|---------|-------------|-------------|--------------|-----------|
| Naming Convention | ⭐⭐⭐⭐⭐ | ⭐⭐⭐ | ⭐⭐⭐⭐ | ✅ Igen (legtöbb eset) |
| JPQL @Query | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ⭐⭐⭐⭐ | ✅ Igen (bonyolult esetekben) |
| Native SQL | ⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⭐⭐⭐⭐⭐ | ⚠️ Csak speciális esetekben |

---

## 💡 Melyiket használd?

### Használd a **Naming Convention**-t (1. módszer), ha:
- Egyszerű keresés (LIKE,Equals, Between, stb.)
- Nem akarod explicit írni a query-t
- Típusbiztonságot akarsz

### Használd a **JPQL Query**-t (2. módszer), ha:
- Bonyolultabb logika kell (JOIN, GROUP BY, stb.)
- Látni akarod a query-t explicit
- Adatbázis függetlenséget akarsz

### Használd a **Native SQL**-t (3. módszer), ha:
- Adatbázis specifikus feature-öket akarsz (pl. PostgreSQL full-text search)
- Teljesítmény optimalizálás kell
- Nagyon bonyolult query

---

## 🎯 A feladatod megoldása

A **"V betűvel kezdődő szerzők könyvei"** feladathoz a legjobb megoldás:

```java
// BookRepository.java
List<Book> findByAuthorNameStartingWith(String prefix);

// Használat
List<Book> books = bookRepository.findByAuthorNameStartingWith("V");
```

**Vagy konkrétan 'V' betűre:**
```java
@Query("SELECT b FROM Book b WHERE b.author.name LIKE 'V%'")
List<Book> findBooksWithAuthorNameStartingWithV();
```

---

## 🚀 Kiegészítő query-k (BÓNUSZ)

Ha további keresési funkciókat akarsz:

```java
// Szerző neve tartalmazza
List<Book> findByAuthorNameContaining(String substring);
// Példa: findByAuthorNameContaining("Hugo") → Viktor Hugo

// Szerző neve végződik
List<Book> findByAuthorNameEndingWith(String suffix);
// Példa: findByAuthorNameEndingWith("Hugo") → Viktor Hugo

// Szerző neve pontosan egyezik (case insensitive)
List<Book> findByAuthorNameIgnoreCase(String name);

// Több feltétel kombinálása
List<Book> findByAuthorNameStartingWithAndPublishYearGreaterThan(String prefix, Integer year);
// Példa: V-vel kezdődő szerzők + 1900 után megjelent könyvek
```

---

## 📝 Összefoglalás

✅ **3 módszer** implementálva  
✅ **2 REST endpoint** a teszteléshez  
✅ **Flexibilis** - bármilyen betűvel kereshetsz  
✅ **Konkrét** - 'V' betűre dedikált endpoint

**Használat Postman-ben:**
```
GET /rest/book/search/by-author?prefix=V
GET /rest/book/search/author-starts-with-v
```

🎉 **Kész vagy! Próbáld ki!**
