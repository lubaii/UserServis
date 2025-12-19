# Используемые модули Spring Boot

Проект использует следующие модули Spring Boot для полноценной работы приложения:

## 1. Spring Boot Starter Web (`spring-boot-starter-web`)

**Назначение:** Предоставляет веб-функциональность для создания REST API.

**Что включает:**
- Spring MVC для обработки HTTP запросов
- Встроенный Tomcat сервер
- JSON сериализация/десериализация (Jackson)
- Поддержка REST контроллеров

**Использование в проекте:**
- `UserController` - REST контроллер для работы с пользователями
- `GlobalExceptionHandler` - глобальный обработчик исключений
- Эндпоинты доступны по адресу: `http://localhost:8080/api/users`

**Примеры использования:**
```bash
# Создание пользователя
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","age":30}'

# Получение всех пользователей
curl http://localhost:8080/api/users

# Получение пользователя по ID
curl http://localhost:8080/api/users/1

# Обновление пользователя
curl -X PUT http://localhost:8080/api/users/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane Doe","email":"jane@example.com","age":25}'

# Удаление пользователя
curl -X DELETE http://localhost:8080/api/users/1
```

## 2. Spring Boot Starter Data JPA (`spring-boot-starter-data-jpa`)

**Назначение:** Предоставляет функциональность для работы с базами данных через JPA/Hibernate.

**Что включает:**
- Hibernate ORM
- Spring Data JPA репозитории
- Автоматическая настройка EntityManager
- Транзакционность

**Использование в проекте:**
- `UserRepository` - интерфейс Spring Data JPA для работы с пользователями
- `User` entity - JPA сущность
- Автоматическое создание/обновление схемы БД

**Примеры использования:**
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

## 3. Spring Boot Starter Validation (`spring-boot-starter-validation`)

**Назначение:** Предоставляет валидацию данных на основе Bean Validation (JSR-303).

**Что включает:**
- Аннотации валидации (`@NotNull`, `@NotBlank`, `@Min`, `@Max`, `@Email` и т.д.)
- Автоматическая валидация в REST контроллерах
- Обработка ошибок валидации

**Использование в проекте:**
- Валидация DTO в `UserController`
- Валидация параметров методов
- Автоматическая обработка ошибок валидации

**Примеры использования:**
```java
@PostMapping
public ResponseEntity<User> createUser(@Valid @RequestBody UserCreateRequest request) {
    // request автоматически валидируется перед выполнением метода
}
```

## 4. Spring Boot Starter Actuator (`spring-boot-starter-actuator`)

**Назначение:** Предоставляет endpoints для мониторинга и управления приложением.

**Что включает:**
- Health checks (`/actuator/health`)
- Метрики приложения (`/actuator/metrics`)
- Информация о приложении (`/actuator/info`)

**Использование в проекте:**
- Мониторинг состояния приложения
- Проверка здоровья БД
- Метрики производительности

**Примеры использования:**
```bash
# Проверка здоровья приложения
curl http://localhost:8080/actuator/health

# Получение метрик
curl http://localhost:8080/actuator/metrics

# Информация о приложении
curl http://localhost:8080/actuator/info
```

## 5. Spring Boot Starter Test (`spring-boot-starter-test`)

**Назначение:** Предоставляет все необходимые зависимости для тестирования.

**Что включает:**
- JUnit 5
- Mockito
- Spring Test Context
- AssertJ
- Hamcrest

**Использование в проекте:**
- Unit тесты (`UserServiceTest`)
- Интеграционные тесты (`UserRepositoryIntegrationTest`)
- Тестирование REST контроллеров

## Конфигурация

Все настройки модулей находятся в `src/main/resources/application.properties`:

```properties
# Web Configuration
server.port=8080

# Actuator Configuration
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=when-authorized

# JPA Configuration
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## Запуск приложения

### Консольный режим (по умолчанию)
```bash
mvn spring-boot:run
# или
java -jar target/user-service-1.0-SNAPSHOT.jar
```

### Веб-режим (REST API)
Приложение автоматически запускает встроенный Tomcat сервер на порту 8080.
REST API доступен по адресу: `http://localhost:8080/api/users`

### Отключение веб-сервера (только консоль)
Раскомментируйте в `Main.java`:
```java
app.setWebApplicationType(WebApplicationType.NONE);
```

## Преимущества использования модулей Spring Boot

1. **Автоконфигурация** - Spring Boot автоматически настраивает все компоненты
2. **Упрощенная разработка** - меньше boilerplate кода
3. **Production-ready** - встроенные возможности для production окружения
4. **Интеграция** - все модули работают вместе из коробки
5. **Тестирование** - удобные инструменты для тестирования

