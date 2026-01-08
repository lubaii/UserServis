# API Документация - User Service

REST API для управления пользователями. Все endpoints используют DTO вместо Entity для изоляции внутренней структуры данных.

## Базовый URL
```
http://localhost:8080/api/users
```

## Endpoints

### 1. Создание пользователя
**POST** `/api/users`

**Request Body:**
```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "age": 30
}
```

**Response:** `201 Created`
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "age": 30,
  "createdAt": "2024-12-11T10:30:00"
}
```

**Валидация:**
- `name`: обязательное поле, максимум 100 символов
- `email`: обязательное поле, валидный email, максимум 100 символов
- `age`: обязательное поле, от 0 до 150

**Пример запроса:**
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "age": 30
  }'
```

---

### 2. Получение пользователя по ID
**GET** `/api/users/{id}`

**Path Parameters:**
- `id` (Long): ID пользователя (минимум 1)

**Response:** `200 OK`
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "age": 30,
  "createdAt": "2024-12-11T10:30:00"
}
```

**Ошибки:**
- `400 Bad Request`: невалидный ID
- `404 Not Found`: пользователь не найден (возвращается как 400 с сообщением)

**Пример запроса:**
```bash
curl http://localhost:8080/api/users/1
```

---

### 3. Получение всех пользователей
**GET** `/api/users`

**Response:** `200 OK`
```json
[
  {
    "id": 1,
    "name": "John Doe",
    "email": "john@example.com",
    "age": 30,
    "createdAt": "2024-12-11T10:30:00"
  },
  {
    "id": 2,
    "name": "Jane Smith",
    "email": "jane@example.com",
    "age": 25,
    "createdAt": "2024-12-11T11:00:00"
  }
]
```

**Пример запроса:**
```bash
curl http://localhost:8080/api/users
```

---

### 4. Обновление пользователя
**PUT** `/api/users/{id}`

**Path Parameters:**
- `id` (Long): ID пользователя (минимум 1)

**Request Body:**
```json
{
  "name": "John Updated",
  "email": "john.updated@example.com",
  "age": 31
}
```

Все поля опциональны. Обновляются только переданные поля.

**Response:** `200 OK`
```json
{
  "id": 1,
  "name": "John Updated",
  "email": "john.updated@example.com",
  "age": 31,
  "createdAt": "2024-12-11T10:30:00"
}
```

**Валидация:**
- `name`: максимум 100 символов (если указано)
- `email`: валидный email, максимум 100 символов (если указано)
- `age`: от 0 до 150 (если указано)

**Пример запроса:**
```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Updated",
    "email": "john.updated@example.com",
    "age": 31
  }'
```

**Частичное обновление:**
```bash
# Обновить только имя
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name": "New Name"}'
```

---

### 5. Удаление пользователя
**DELETE** `/api/users/{id}`

**Path Parameters:**
- `id` (Long): ID пользователя (минимум 1)

**Response:** `204 No Content`

**Ошибки:**
- `400 Bad Request`: невалидный ID или пользователь не найден

**Пример запроса:**
```bash
curl -X DELETE http://localhost:8080/api/users/1
```

---

## Обработка ошибок

### Валидация ошибок (400 Bad Request)
```json
{
  "error": "Validation Failed",
  "fieldErrors": {
    "email": "Email must be a valid email address",
    "age": "Age must be at least 0"
  }
}
```

### Бизнес-логика ошибки (400 Bad Request)
```json
{
  "error": "Bad Request",
  "message": "User with this email already exists"
}
```

### Внутренняя ошибка сервера (500 Internal Server Error)
```json
{
  "error": "Internal Server Error",
  "message": "Error message"
}
```

---

## DTO Структура

### UserDTO (Response)
Используется для всех ответов API.

```java
{
  "id": Long,
  "name": String,
  "email": String,
  "age": Integer,
  "createdAt": LocalDateTime
}
```

### UserCreateDTO (Request для создания)
```java
{
  "name": String (обязательное, max 100),
  "email": String (обязательное, валидный email, max 100),
  "age": Integer (обязательное, 0-150)
}
```

### UserUpdateDTO (Request для обновления)
Все поля опциональны.

```java
{
  "name": String (опционально, max 100),
  "email": String (опционально, валидный email, max 100),
  "age": Integer (опционально, 0-150)
}
```

---

## Архитектура

### Слои приложения:
1. **Controller** (`UserController`) - обрабатывает HTTP запросы, использует DTO
2. **Mapper** (`UserMapper`) - конвертирует между Entity и DTO
3. **Service** (`UserService`) - бизнес-логика, работает с Entity
4. **Repository** (`UserRepository`) - доступ к данным, работает с Entity
5. **Entity** (`User`) - модель данных, не экспортируется в API

### Преимущества использования DTO:
- ✅ Изоляция внутренней структуры данных
- ✅ Гибкость изменения Entity без влияния на API
- ✅ Контроль над данными, возвращаемыми клиенту
- ✅ Возможность добавления вычисляемых полей в DTO
- ✅ Безопасность (скрытие внутренних полей)

---

## Примеры использования

### Полный цикл работы с пользователем:

```bash
# 1. Создание пользователя
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "age": 30
  }'

# Ответ: {"id":1,"name":"John Doe","email":"john@example.com","age":30,"createdAt":"..."}

# 2. Получение пользователя
curl http://localhost:8080/api/users/1

# 3. Обновление пользователя
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Updated",
    "age": 31
  }'

# 4. Получение всех пользователей
curl http://localhost:8080/api/users

# 5. Удаление пользователя
curl -X DELETE http://localhost:8080/api/users/1
```

