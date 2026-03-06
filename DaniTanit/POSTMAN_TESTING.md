# 📬 Postman Tesztelési Útmutató

## 🔧 Változtatások összefoglalása

### ✅ Javított komponensek:

1. **BookConverter** - Null check-ek hozzáadva, biztonságos `.orElseThrow()` használat
2. **BookService** - Unsafe `.get()` eltávolítva, update metódus javítva
3. **Publish entitás** - `@PrePersist` callback automatikus dátum beállításhoz
4. **PublishDto, PublishConverter** - DTO pattern implementálva
5. **PublishService, PublishController** - Teljes REST API DTO-val

---

## 📚 1. PUBLISH API - Publikálás létrehozása

### Endpoint:
```
POST http://localhost:8080/rest/publish/create
```

### Headers:
```
Content-Type: application/json
```

### Request Body példák:

#### A) Csak kötelező mezők (dátum automatikus)
```json
{
  "bookId": 1,
  "authorId": 1
}
```
**Magyarázat:** A `publishDate` automatikusan beállítódik `LocalDateTime.now()`-ra a `@PrePersist` callback miatt.

---

#### B) Saját dátummal
```json
{
  "bookId": 1,
  "authorId": 1,
  "publishDate": "2024-03-15T10:30:00"
}
```
**Magyarázat:** Ha megadsz dátumot, azt használja, nem írja felül.

---

#### C) Teljes példa több publikálással
```json
{
  "bookId": 2,
  "authorId": 1,
  "publishDate": "2023-06-20T14:00:00"
}
```

---

### Response:
```json
201 CREATED

Body:
1
```
**Visszaadja az újonnan létrehozott Publish ID-jét.**

---

## 📖 2. KÖNYV LÉTREHOZÁSA (ahhoz kell Publish-hez)

Először hozz létre egy könyvet és egy szerzőt, hogy legyen mivel publikálni!

### A) Szerző létrehozása
```
POST http://localhost:8080/rest/author/create
```

```json
{
  "name": "J.R.R. Tolkien"
}
```

**Response:** `1` (Author ID)

---

### B) Könyv létrehozása
```
POST http://localhost:8080/rest/book/create
```

```json
{
  "name": "The Hobbit",
  "publishYear": 1937,
  "authorId": 1,
  "genreIds": []
}
```

**Response:** `1` (Book ID)

---

### C) Most már létrehozhatod a Publish-t!
```
POST http://localhost:8080/rest/publish/create
```

```json
{
  "bookId": 1,
  "authorId": 1
}
```

---

## 🔍 3. PUBLISH LEKÉRDEZÉSEK

### Összes publikálás lekérdezése:
```
GET http://localhost:8080/rest/publish/all
```

**Response:**
```json
[
  {
    "id": 1,
    "bookId": 1,
    "authorId": 1,
    "publishDate": "2026-03-03T10:30:45.123456"
  },
  {
    "id": 2,
    "bookId": 2,
    "authorId": 1,
    "publishDate": "2023-06-20T14:00:00"
  }
]
```

---

### Egy publikálás lekérdezése ID alapján:
```
GET http://localhost:8080/rest/publish/1
```

**Response:**
```json
{
  "id": 1,
  "bookId": 1,
  "authorId": 1,
  "publishDate": "2026-03-03T10:30:45.123456"
}
```

---

### Publikálás törlése:
```
DELETE http://localhost:8080/rest/publish/1
```

**Response:** `204 NO CONTENT`

---

## 🧪 4. TELJES TESZTELÉSI FOLYAMAT

### 1. lépés - Szerző létrehozása
```http
POST /rest/author/create
{
  "name": "George R.R. Martin"
}
```
→ Kapott Author ID: **1**

---

### 2. lépés - Műfaj létrehozása (ha van GenreController)
```http
POST /rest/genre/create
{
  "type": "Fantasy"
}
```
→ Kapott Genre ID: **1**

---

### 3. lépés - Könyv létrehozása műfajokkal
```http
POST /rest/book/create
{
  "name": "A Game of Thrones",
  "publishYear": 1996,
  "authorId": 1,
  "genreIds": [1]
}
```
→ Kapott Book ID: **1**

---

### 4. lépés - Publikálás létrehozása (AUTOMATIKUS DÁTUMMAL)
```http
POST /rest/publish/create
{
  "bookId": 1,
  "authorId": 1
}
```
→ Kapott Publish ID: **1**

✅ **A `publishDate` automatikusan beállítódik a létrehozás időpontjára!**

---

### 5. lépés - Ellenőrzés
```http
GET /rest/publish/1
```

**Response:**
```json
{
  "id": 1,
  "bookId": 1,
  "authorId": 1,
  "publishDate": "2026-03-03T11:45:30.123456"  ← Automatikusan generálódott!
}
```

---

## ⚙️ 5. PUBLISH MŰKÖDÉSI MECHANIZMUS

### Mi történik a háttérben:

1. **Request érkezik:**
   ```json
   { "bookId": 1, "authorId": 1 }
   ```

2. **PublishController** átadja a `PublishService`-nek

3. **PublishService** meghívja a `PublishConverter.toEntity()`-t
   - Ellenőrzi, hogy létezik-e a Book (ID: 1)
   - Ellenőrzi, hogy létezik-e az Author (ID: 1)
   - Létrehoz egy Publish entitást

4. **Publish entitás mentése előtt** (`@PrePersist` callback)
   ```java
   @PrePersist
   protected void onCreate() {
       if (publishDate == null) {
           publishDate = LocalDateTime.now();  ← Itt állítódik be!
       }
   }
   ```

5. **Publish elmentve** az adatbázisba automatikus dátummal

---

## ❌ 6. HIBAKEZELÉS

### Ha nem létező Book ID-t adsz meg:
```json
{
  "bookId": 999,
  "authorId": 1
}
```

**Response:**
```json
500 Internal Server Error

{
  "message": "Book not found with id: 999"
}
```

---

### Ha hiányzik a kötelező mező:
```json
{
  "authorId": 1
}
```

**Response:**
```json
500 Internal Server Error

{
  "message": "Book ID is required"
}
```

---

## 📋 7. ÖSSZEFOGLALÓ

### Publish létrehozásához szükséges:
1. ✅ Létező Book (bookId)
2. ✅ Létező Author (authorId)
3. ⚠️ publishDate (OPCIONÁLIS - automatikus)

### Automatikus dátum beállítás:
- Ha **NEM** adsz meg `publishDate`-et → **automatikusan beállítódik**
- Ha **megadsz** `publishDate`-et → **azt használja**

### Postman tesztelési sorrend:
1. Hozz létre Author-t
2. Hozz létre Book-ot
3. Hozz létre Publish-t (automatikus dátummal)
4. Ellenőrizd GET /rest/publish/all-lal

---

## 🎯 GYORS TESZT PÉLDA

**1. Szerző:**
```bash
curl -X POST http://localhost:8080/rest/author/create \
  -H "Content-Type: application/json" \
  -d '{"name": "J.K. Rowling"}'
```

**2. Könyv:**
```bash
curl -X POST http://localhost:8080/rest/book/create \
  -H "Content-Type: application/json" \
  -d '{"name": "Harry Potter", "publishYear": 1997, "authorId": 1, "genreIds": []}'
```

**3. Publikálás (automatikus dátum):**
```bash
curl -X POST http://localhost:8080/rest/publish/create \
  -H "Content-Type: application/json" \
  -d '{"bookId": 1, "authorId": 1}'
```

**4. Lekérdezés:**
```bash
curl http://localhost:8080/rest/publish/1
```

---

🎉 **Kész! A Publish API működik automatikus dátum beállítással!**
