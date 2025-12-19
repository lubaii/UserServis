# Интеграционные тесты для проверки отправки почты

## Быстрый старт

### Unit тесты (работают сразу):
```bash
cd "/home/luba/Documents/Обучение/UserServis/notification-service"
mvn test -Dtest=EmailNotificationServiceTest
mvn test -Dtest=NotificationControllerTest
```

### Все тесты:
```bash
mvn test
```

## Что тестируется

### ✅ Unit тесты (с мокированием)

1. **EmailNotificationServiceTest** - проверяет логику отправки писем
   - Отправка письма о создании пользователя
   - Отправка письма об удалении пользователя
   - Обработка ошибок

2. **NotificationControllerTest** - проверяет REST API
   - POST `/api/notifications/send` с операцией CREATE
   - POST `/api/notifications/send` с операцией DELETE
   - Валидация входных данных

### ⚠️ Интеграционные тесты (требуют настройки GreenMail)

Интеграционные тесты используют GreenMail (встроенный SMTP сервер) для проверки реальной отправки писем. 

**Примечание:** Из-за конфликта между `jakarta.mail` (Spring Boot 3) и `javax.mail` (GreenMail 1.6.15) интеграционные тесты могут требовать дополнительной настройки.

## Структура тестов

```
notification-service/src/test/java/com/notificationservice/
├── service/
│   ├── EmailNotificationServiceTest.java          # Unit тесты ✅
│   └── EmailNotificationServiceIntegrationTest.java # Интеграционные тесты ⚠️
├── controller/
│   ├── NotificationControllerTest.java            # Unit тесты ✅
│   └── NotificationControllerIntegrationTest.java  # Интеграционные тесты ⚠️
└── config/
    └── TestMailConfig.java                        # Конфигурация для тестов
```

## Примеры использования

### Проверка отправки письма о создании пользователя:
```java
@Test
void testSendUserCreatedEmail() {
    String recipientEmail = "user@example.com";
    emailNotificationService.sendUserCreatedEmail(recipientEmail);
    
    // Проверка, что письмо было отправлено с правильными параметрами
    verify(mailSender).send(argThat(message -> 
        "Создание аккаунта".equals(message.getSubject()) &&
        recipientEmail.equals(message.getTo()[0])
    ));
}
```

### Проверка REST API:
```java
@Test
void testSendEmail_CreateOperation() {
    EmailRequest request = new EmailRequest();
    request.setOperation("CREATE");
    request.setEmail("user@example.com");
    
    ResponseEntity<Void> response = controller.sendEmail(request);
    
    assertEquals(HttpStatus.OK, response.getStatusCode());
    verify(emailService).sendUserCreatedEmail("user@example.com");
}
```

## Зависимости

Все необходимые зависимости уже добавлены в `pom.xml`:
- `spring-boot-starter-test` - для Spring Boot тестов
- `greenmail-junit5` - для интеграционных тестов с SMTP
- Mockito (входит в spring-boot-starter-test) - для мокирования

## Дополнительная информация

Подробная документация: см. `TESTING.md`

