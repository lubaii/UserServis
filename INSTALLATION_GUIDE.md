# Инструкция по установке и настройке User Service и Notification Service

## Содержание
1. [Требования](#требования)
2. [Установка зависимостей](#установка-зависимостей)
3. [Настройка PostgreSQL](#настройка-postgresql)
4. [Настройка Kafka через Docker Compose](#настройка-kafka-через-docker-compose)
5. [Настройка User Service](#настройка-user-service)
6. [Настройка Notification Service](#настройка-notification-service)
7. [Запуск всех сервисов](#запуск-всех-сервисов)
8. [Проверка работы](#проверка-работы)
9. [Устранение проблем](#устранение-проблем)

---

## Требования

- **Java 17** или выше
- **Maven 3.6+**
- **Docker** и **Docker Compose**
- **PostgreSQL 12+**
- Почтовый аккаунт (Yandex, Gmail или другой SMTP-сервер)

---

## Установка зависимостей

### 1. Установка Java 17

#### Ubuntu/Debian:
```bash
sudo apt update
sudo apt install openjdk-17-jdk
java -version  # Проверка версии
```

#### Проверка:
```bash
java -version
# Должно быть: openjdk version "17.x.x"
```

### 2. Установка Maven

#### Ubuntu/Debian:
```bash
sudo apt install maven
mvn -version  # Проверка версии
```

#### Проверка:
```bash
mvn -version
# Должно быть: Apache Maven 3.6.x или выше
```

### 3. Установка Docker и Docker Compose

#### Ubuntu/Debian:
```bash
# Установка Docker
sudo apt update
sudo apt install docker.io docker-compose-plugin

# Добавление пользователя в группу docker (чтобы не использовать sudo)
sudo groupadd docker 2>/dev/null || true
sudo usermod -aG docker $USER
newgrp docker

# Проверка установки
docker --version
docker compose version
```

**Важно:** После добавления в группу `docker` нужно **перезайти в систему** или выполнить `newgrp docker`.

---

## Настройка PostgreSQL

### 1. Установка PostgreSQL

#### Ubuntu/Debian:
```bash
sudo apt update
sudo apt install postgresql postgresql-contrib
sudo systemctl start postgresql
sudo systemctl enable postgresql
```

### 2. Создание базы данных и пользователя

```bash
# Переключение на пользователя postgres
sudo -u postgres psql

# В консоли PostgreSQL выполните:
CREATE DATABASE usersdb;
ALTER USER postgres WITH PASSWORD '1111';  # Или ваш пароль
\q  # Выход из psql
```

### 3. Проверка подключения

```bash
psql -U postgres -d usersdb -h localhost
# Введите пароль при запросе
```

---

## Настройка Kafka через Docker Compose

### 1. Проверка Docker Compose файла

Убедитесь, что файл `docker-compose.yml` находится в корне проекта:

```bash
cd "/home/luba/Documents/Обучение/UserServis"
ls docker-compose.yml
```

### 2. Запуск Kafka и Zookeeper

```bash
# Из корня проекта
cd "/home/luba/Documents/Обучение/UserServis"

# Запуск в фоновом режиме
sudo docker compose up -d

# Проверка статуса
docker compose ps
# Должны быть запущены: kafka и zookeeper
```

### 3. Проверка работы Kafka

```bash
# Проверка топиков
docker compose exec kafka kafka-topics --bootstrap-server localhost:9092 --list
# Должен быть виден топик: user-events (создастся автоматически при первом сообщении)
```

### 4. Остановка Kafka (если нужно)

```bash
docker compose down
```

---

## Настройка User Service

### 1. Настройка подключения к базе данных

Откройте файл `src/main/resources/application.properties` и проверьте настройки:

```properties
spring.datasource.url=${DB_URL:jdbc:postgresql://localhost:5432/usersdb}
spring.datasource.username=${DB_USERNAME:postgres}
spring.datasource.password=${DB_PASSWORD:1111}
```

**Вариант 1:** Использовать переменные окружения (рекомендуется):
```bash
export DB_URL=jdbc:postgresql://localhost:5432/usersdb
export DB_USERNAME=postgres
export DB_PASSWORD=1111  # Ваш пароль от PostgreSQL
```

**Вариант 2:** Изменить значения напрямую в `application.properties`.

### 2. Настройка Kafka в User Service

Проверьте настройки Kafka в `src/main/resources/application.properties`:

```properties
spring.kafka.bootstrap-servers=${KAFKA_BOOTSTRAP_SERVERS:localhost:9092}
kafka.topic.user-events=user-events
```

Эти настройки уже должны быть правильными, если Kafka запущен на `localhost:9092`.

### 3. Сборка проекта

```bash
cd "/home/luba/Documents/Обучение/UserServis"
mvn clean install
```

---

## Настройка Notification Service

### 1. Настройка Kafka Consumer

Файл `notification-service/src/main/resources/application.properties` уже настроен:

```properties
spring.kafka.bootstrap-servers=localhost:9092
spring.kafka.consumer.group-id=notification-service
kafka.topic.user-events=user-events
```

### 2. Настройка почты (Yandex)

Откройте `notification-service/src/main/resources/application.properties` и настройте:

```properties
# Настройки почты Yandex
spring.mail.host=smtp.yandex.ru
spring.mail.port=587
spring.mail.username=ВАШ_EMAIL@yandex.ru
spring.mail.password=ВАШ_ПАРОЛЬ_ИЛИ_ПАРОЛЬ_ПРИЛОЖЕНИЯ
spring.mail.from=ВАШ_EMAIL@yandex.ru
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.ssl.enable=false
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
```

**Важно для Yandex:**
- Если включена двухфакторная аутентификация, нужен **пароль приложения**
- Получить пароль приложения: https://id.yandex.ru/security → "Пароли приложений"
- Адрес отправителя (`spring.mail.from`) должен совпадать с `spring.mail.username`

**Альтернатива - Gmail:**
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=ВАШ_EMAIL@gmail.com
spring.mail.password=ПАРОЛЬ_ПРИЛОЖЕНИЯ_GMAIL
spring.mail.from=ВАШ_EMAIL@gmail.com
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

### 3. Сборка Notification Service

```bash
cd "/home/luba/Documents/Обучение/UserServis/notification-service"
mvn clean install
```

---

## Запуск всех сервисов

### Порядок запуска (ВАЖНО!):

1. **PostgreSQL** (должен быть запущен)
2. **Kafka** (через Docker Compose)
3. **Notification Service** (должен быть запущен первым, чтобы подписаться на топик)
4. **User Service**

---

### Быстрый запуск (скопируйте команды по порядку)

#### Шаг 1: Проверка PostgreSQL
```bash
# Убедитесь, что PostgreSQL запущен
sudo systemctl status postgresql
# Если не запущен:
sudo systemctl start postgresql
```

#### Шаг 2: Запуск Kafka и Zookeeper
```bash
# Перейдите в корень проекта
cd "/home/luba/Documents/Обучение/UserServis"

# Запустите Kafka в фоновом режиме
sudo docker compose up -d

# Подождите 10-15 секунд для полного запуска Kafka
sleep 15

# Проверьте статус (должны быть: kafka и zookeeper в статусе Up)
docker compose ps
```

**Ожидаемый результат:**
```
NAME        STATUS
kafka       Up (healthy)
zookeeper   Up
```

#### Шаг 3: Настройка переменных окружения для User Service
```bash
# Откройте новый терминал или выполните в текущем
export DB_URL=jdbc:postgresql://localhost:5432/usersdb
export DB_USERNAME=postgres
export DB_PASSWORD=1111  # Замените на ваш пароль PostgreSQL
```

#### Шаг 4: Запуск Notification Service
**Откройте ТЕРМИНАЛ 1:**
```bash
# Перейдите в папку notification-service
cd "/home/luba/Documents/Обучение/UserServis/notification-service"

# Запустите сервис
mvn spring-boot:run
```

**Дождитесь сообщения в логах:**
```
Tomcat initialized with port 8081 (http)
Started NotificationServiceApplication
```

**Оставьте этот терминал открытым!**

#### Шаг 5: Запуск User Service
**Откройте ТЕРМИНАЛ 2:**
```bash
# Перейдите в корень проекта
cd "/home/luba/Documents/Обучение/UserServis"

# Установите переменные окружения (если ещё не установлены)
export DB_URL=jdbc:postgresql://localhost:5432/usersdb
export DB_USERNAME=postgres
export DB_PASSWORD=1111  # Замените на ваш пароль PostgreSQL

# Запустите сервис
mvn spring-boot:run
```

**Дождитесь сообщения в логах:**
```
Tomcat initialized with port 8080 (http)
Started Main
```

**Оставьте этот терминал открытым!**

---

### Использование готовых скриптов

В корне проекта уже есть готовые скрипты:

**Запуск всех сервисов:**
```bash
cd "/home/luba/Documents/Обучение/UserServis"
./start-all.sh
```

После выполнения скрипта откройте два терминала и запустите сервисы как указано в выводе скрипта.

**Остановка всех сервисов:**
```bash
cd "/home/luba/Documents/Обучение/UserServis"
./stop-all.sh
```

---

### Полный скрипт запуска (уже создан в проекте)

Файл `start-all.sh` уже создан в корне проекта:

```bash
#!/bin/bash

echo "=== Запуск всех сервисов ==="

# Шаг 1: Проверка PostgreSQL
echo "1. Проверка PostgreSQL..."
sudo systemctl start postgresql 2>/dev/null || true
sleep 2

# Шаг 2: Запуск Kafka
echo "2. Запуск Kafka..."
cd "/home/luba/Documents/Обучение/UserServis"
sudo docker compose up -d
echo "Ожидание запуска Kafka (15 секунд)..."
sleep 15
docker compose ps

# Шаг 3: Установка переменных окружения
echo "3. Настройка переменных окружения..."
export DB_URL=jdbc:postgresql://localhost:5432/usersdb
export DB_USERNAME=postgres
export DB_PASSWORD=1111  # ЗАМЕНИТЕ НА ВАШ ПАРОЛЬ!

echo ""
echo "=== Сервисы готовы к запуску ==="
echo ""
echo "ТЕРМИНАЛ 1 - Запустите Notification Service:"
echo "  cd notification-service && mvn spring-boot:run"
echo ""
echo "ТЕРМИНАЛ 2 - Запустите User Service:"
echo "  mvn spring-boot:run"
echo ""
echo "Переменные окружения уже установлены в текущей сессии."
```

**Использование:**
```bash
chmod +x start-all.sh
./start-all.sh
```

---

### Проверка запуска всех сервисов

После запуска всех сервисов проверьте:

```bash
# Проверка Kafka
docker compose ps
# Должны быть: kafka (Up, healthy) и zookeeper (Up)

# Проверка портов
netstat -tuln | grep -E '8080|8081|9092'
# Должны быть:
# 8080 - User Service
# 8081 - Notification Service  
# 9092 - Kafka

# Проверка через curl
curl http://localhost:8080/actuator/health  # User Service
curl http://localhost:8081/actuator/health  # Notification Service (если есть actuator)
```

---

### Остановка всех сервисов

**Вариант 1: Использование скрипта (рекомендуется)**
```bash
cd "/home/luba/Documents/Обучение/UserServis"
./stop-all.sh
```

**Вариант 2: Ручная остановка**
1. **Терминал 1 и 2:** Нажмите `Ctrl+C` для остановки User Service и Notification Service
2. **Остановите Kafka:**
```bash
cd "/home/luba/Documents/Обучение/UserServis"
docker compose down
```

---

## Проверка работы

### 1. Проверка через REST API

#### Создание пользователя:
```bash
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Тестовый Пользователь",
    "email": "test@example.com",
    "age": 25
  }'
```

**Ожидаемый результат:**
- В логах `user-service`: `User event sent successfully: UserEvent{operation='CREATE'...}`
- В логах `notification-service`: 
  - `Received user event from Kafka: operation=CREATE, email=test@example.com`
  - `Sending user created email to: test@example.com`
  - `User created email sent successfully to: test@example.com`
- Письмо приходит на `test@example.com` с текстом: "Здравствуйте! Ваш аккаунт на сайте ваш сайт был успешно создан."

#### Удаление пользователя:
```bash
# Сначала узнайте ID пользователя
curl http://localhost:8080/api/users

# Затем удалите (замените {id} на реальный ID)
curl -X DELETE http://localhost:8080/api/users/{id}
```

**Ожидаемый результат:**
- В логах `notification-service`: 
  - `Received user event from Kafka: operation=DELETE, email=test@example.com`
  - `Sending user deleted email to: test@example.com`
  - `User deleted email sent successfully to: test@example.com`
- Письмо приходит с текстом: "Здравствуйте! Ваш аккаунт был удалён."

### 2. Проверка через REST API Notification Service

Можно отправить письмо напрямую через API `notification-service`:

```bash
curl -X POST http://localhost:8081/api/notifications/send \
  -H "Content-Type: application/json" \
  -d '{
    "operation": "CREATE",
    "email": "test@example.com"
  }'
```

### 3. Проверка Kafka

```bash
# Просмотр сообщений в топике
docker compose exec kafka kafka-console-consumer \
  --bootstrap-server localhost:9092 \
  --topic user-events \
  --from-beginning \
  --max-messages 5
```

---

## Устранение проблем

### Проблема: Port 8080/8081 already in use

**Решение:**
```bash
# Найти процесс, занимающий порт
lsof -i :8080  # или :8081
# Остановить процесс
kill <PID>
```

### Проблема: PostgreSQL password authentication failed

**Решение:**
```bash
# Изменить пароль пользователя postgres
sudo -u postgres psql
ALTER USER postgres WITH PASSWORD '1111';
\q

# Или обновить переменную окружения
export DB_PASSWORD=ваш_пароль
```

### Проблема: Kafka не запускается

**Решение:**
```bash
# Проверка статуса
docker compose ps

# Перезапуск
docker compose down
docker compose up -d

# Просмотр логов
docker compose logs kafka
```

### Проблема: MailAuthenticationException (Yandex)

**Возможные причины:**
1. Неправильный пароль
2. Нужен пароль приложения (если включена 2FA)

**Решение:**
- Проверьте пароль в `application.properties`
- Если включена 2FA, создайте пароль приложения: https://id.yandex.ru/security
- Убедитесь, что `spring.mail.from` совпадает с `spring.mail.username`

### Проблема: Bad address mailbox syntax

**Решение:**
- Убедитесь, что в `EmailNotificationService` установлен `message.setFrom("ваш_email@yandex.ru")`
- Проверьте, что адрес отправителя совпадает с адресом аутентификации

### Проблема: События не доходят до Notification Service

**Проверка:**
1. Kafka запущен: `docker compose ps`
2. Notification Service запущен и слушает порт 8081
3. В логах `notification-service` есть сообщения о подключении к Kafka
4. Проверьте настройки `spring.kafka.bootstrap-servers=localhost:9092`

**Решение:**
```bash
# Перезапустить оба сервиса
# 1. Остановить оба сервиса (Ctrl+C)
# 2. Запустить сначала notification-service, затем user-service
```

---

## Структура проекта

```
UserServis/
├── docker-compose.yml          # Конфигурация Kafka и Zookeeper
├── pom.xml                     # Родительский POM (если multi-module)
├── src/                        # User Service
│   └── main/
│       ├── java/com/userservice/
│       └── resources/
│           └── application.properties
└── notification-service/       # Notification Service модуль
    ├── pom.xml
    └── src/
        └── main/
            ├── java/com/notificationservice/
            └── resources/
                └── application.properties
```

---

## Полезные команды

### Остановка всех сервисов:
```bash
# Остановить User Service и Notification Service (Ctrl+C в терминалах)
# Остановить Kafka
docker compose down
```

### Просмотр логов Kafka:
```bash
docker compose logs -f kafka
```

### Очистка топика Kafka (если нужно):
```bash
docker compose exec kafka kafka-topics --bootstrap-server localhost:9092 --delete --topic user-events
```

### Проверка подключения к PostgreSQL:
```bash
psql -U postgres -d usersdb -h localhost -c "SELECT COUNT(*) FROM users;"
```

---

## Контакты и поддержка

При возникновении проблем проверьте:
1. Логи сервисов (терминалы, где запущены сервисы)
2. Логи Kafka: `docker compose logs kafka`
3. Настройки в `application.properties` обоих сервисов
4. Статус всех сервисов: `docker compose ps`

---

**Успешной работы! 🚀**

