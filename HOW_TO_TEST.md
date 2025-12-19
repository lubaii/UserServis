# Как проверить работу приложения

## Быстрая проверка

### 1. Проверка здоровья приложения
```bash
curl http://localhost:8080/actuator/health
```
Ожидаемый результат: `{"status":"UP"}`

### 2. Использование скрипта тестирования
```bash
./test_api.sh
```
Скрипт автоматически протестирует все CRUD операции.

## Ручное тестирование

### 1. Health Check
```bash
curl http://localhost:8080/actuator/health
```

### 2. Создание пользователя
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","age":30}'
```

### 3. Получение всех пользователей
```bash
curl http://localhost:8080/api/users
```

### 4. Получение пользователя по ID
```bash
curl http://localhost:8080/api/users/1
```

### 5. Обновление пользователя
```bash
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"John Updated","age":31}'
```

### 6. Удаление пользователя
```bash
curl -X DELETE http://localhost:8080/api/users/1
```

### 7. Тест валидации (должен вернуть 400)
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"Test","email":"invalid-email","age":25}'
```

## Проверка через браузер

Откройте в браузере:
- `http://localhost:8080/actuator/health` - проверка здоровья
- `http://localhost:8080/api/users` - получение всех пользователей
- `http://localhost:8080/api/users/1` - получение пользователя по ID

## Проверка логов

```bash
# Просмотр логов в реальном времени
tail -f app.log

# Последние 50 строк логов
tail -50 app.log

# Поиск ошибок
grep ERROR app.log
```

## Проверка статуса приложения

```bash
# Проверить, запущено ли приложение
lsof -i :8080

# Проверить процесс
ps aux | grep "com.userservice.Main" | grep -v grep

# Проверить через curl
curl -s http://localhost:8080/actuator/health
```

## Ожидаемые результаты

### Успешные операции:
- **POST** `/api/users` → `201 Created` с телом пользователя
- **GET** `/api/users` → `200 OK` со списком пользователей
- **GET** `/api/users/{id}` → `200 OK` с данными пользователя
- **PUT** `/api/users/{id}` → `200 OK` с обновленными данными
- **DELETE** `/api/users/{id}` → `204 No Content`

### Ошибки валидации:
- Невалидный email → `400 Bad Request` с описанием ошибки
- Невалидный ID (0 или отрицательный) → `400 Bad Request`
- Пользователь не найден → `400 Bad Request` с сообщением

