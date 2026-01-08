# Интеграционные тесты для Notification Service

## Описание

Интеграционные тесты проверяют отправку электронной почты через различные компоненты сервиса:
- `EmailNotificationService` - сервис отправки писем
- `NotificationController` - REST API для отправки писем
- `UserEventListener` - обработчик событий из Kafka

## Технологии

- **GreenMail** - встроенный SMTP сервер для тестирования отправки почты
- **JUnit 5** - фреймворк для тестирования
- **Spring Boot Test** - интеграция с Spring Boot
- **MockMvc** - тестирование REST контроллеров

## Запуск тестов

### Все тесты:
```bash
cd "/home/luba/Documents/Обучение/UserServis/notification-service"
mvn test
```

### Только unit тесты (быстрые, без SMTP):
```bash
mvn test -Dtest=EmailNotificationServiceTest
mvn test -Dtest=NotificationControllerTest
```

### Интеграционные тесты (требуют GreenMail):
```bash
mvn test -Dtest=EmailNotificationServiceIntegrationTest
mvn test -Dtest=NotificationControllerIntegrationTest
mvn test -Dtest=UserEventListenerIntegrationTest
```

### Конкретный тест:
```bash
mvn test -Dtest=EmailNotificationServiceTest#testSendUserCreatedEmail
```

## Структура тестов

### 1. EmailNotificationServiceTest (Unit тесты с мокированием)

Проверяет логику отправки писем через `EmailNotificationService`:

- ✅ Отправка письма о создании пользователя
- ✅ Отправка письма об удалении пользователя
- ✅ Обработка исключений при отправке

**Что проверяется:**
- Вызов метода `mailSender.send()` с правильными параметрами
- Корректность темы письма
- Корректность адреса получателя
- Содержимое письма

### 2. EmailNotificationServiceIntegrationTest (Интеграционные тесты с GreenMail)

Проверяет отправку писем через реальный SMTP сервер (GreenMail):

- ✅ Отправка письма о создании пользователя
- ✅ Отправка письма об удалении пользователя
- ✅ Отправка нескольких писем подряд

**Что проверяется:**
- Фактическая отправка письма через SMTP
- Корректность темы письма
- Корректность адреса получателя
- Содержимое письма

### 3. NotificationControllerTest (Unit тесты)

Проверяет логику REST API `/api/notifications/send`:

- ✅ Отправка письма через API (CREATE операция)
- ✅ Отправка письма через API (DELETE операция)
- ✅ Обработка некорректной операции
- ✅ Обработка отсутствующего email
- ✅ Обработка пустого email

**Что проверяется:**
- HTTP статус ответа
- Вызов правильного метода сервиса

### 4. NotificationControllerIntegrationTest (Интеграционные тесты)

Проверяет REST API `/api/notifications/send` с реальным SMTP:

- ✅ Отправка письма через API (CREATE операция)
- ✅ Отправка письма через API (DELETE операция)
- ✅ Обработка некорректной операции
- ✅ Обработка отсутствующего email
- ✅ Обработка пустого email

**Что проверяется:**
- HTTP статус ответа
- Фактическая отправка письма через GreenMail

### 5. UserEventListenerIntegrationTest (Интеграционные тесты)

Проверяет обработку событий из Kafka:

- ✅ Обработка события CREATE
- ✅ Обработка события DELETE
- ✅ Обработка некорректного события (null email)
- ✅ Обработка неизвестной операции
- ✅ Обработка null события

**Что проверяется:**
- Корректная обработка события из Kafka
- Отправка соответствующего письма

## Как работают тесты

1. **GreenMail** запускается как встроенный SMTP сервер на порту 3025
2. Spring Boot настраивается на использование этого SMTP сервера
3. Тесты отправляют письма через реальный `JavaMailSender`
4. GreenMail перехватывает письма и позволяет проверить их содержимое
5. После каждого теста GreenMail очищается (`greenMail.reset()`)

## Пример использования

```java
@Test
void testSendUserCreatedEmail() throws MessagingException {
    // Отправка письма
    emailNotificationService.sendUserCreatedEmail("user@example.com");
    
    // Проверка, что письмо было отправлено
    Message[] messages = greenMail.getReceivedMessages();
    assertEquals(1, messages.length);
    
    // Проверка содержимого
    Message message = messages[0];
    assertEquals("Создание аккаунта", message.getSubject());
    assertEquals("user@example.com", 
        message.getRecipients(Message.RecipientType.TO)[0].toString());
}
```

## Настройки тестов

Тестовые настройки находятся в:
- `src/test/resources/application-test.properties`
- `@TestPropertySource` аннотации в тестах

Основные настройки:
```properties
spring.mail.host=localhost
spring.mail.port=3025
spring.mail.username=test@test.com
spring.mail.password=test
```

## Устранение проблем

### Тесты не запускаются

**Проблема:** Порт 3025 занят
```bash
# Проверка порта
lsof -i :3025
# Остановка процесса
kill <PID>
```

### GreenMail не запускается

**Проблема:** Конфликт портов или неправильная конфигурация
- Убедитесь, что порт 3025 свободен
- Проверьте настройки в `TestMailConfig`

### Письма не перехватываются

**Проблема:** Неправильная настройка JavaMailSender
- Проверьте, что используется `TestMailConfig`
- Убедитесь, что `@Import(TestMailConfig.class)` присутствует в тесте

## Дополнительные тесты

Для более полного покрытия можно добавить:

1. **Тесты производительности** - отправка большого количества писем
2. **Тесты с реальным Kafka** - используя Testcontainers
3. **Тесты обработки ошибок** - когда SMTP сервер недоступен
4. **Тесты валидации email** - проверка формата адресов

## Зависимости

Все необходимые зависимости уже добавлены в `pom.xml`:
- `greenmail-junit5` - для GreenMail
- `spring-boot-starter-test` - для Spring Boot тестов
- `spring-kafka-test` - для тестирования Kafka (опционально)

