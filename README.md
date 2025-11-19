# User Service

Консольное приложение на Java для управления пользователями с использованием Hibernate и PostgreSQL.

## Требования

- Java 17 или выше
- Maven 3.6+
- PostgreSQL 12+

## Настройка

### 1. Создание базы данных

Создайте базу данных в PostgreSQL:

```sql
CREATE DATABASE usersdb;
```

### 2. Настройка подключения

Отредактируйте файл `src/main/resources/hibernate.cfg.xml` и укажите ваши параметры подключения:

```xml
<property name="hibernate.connection.url">jdbc:postgresql://localhost:5432/usersdb</property>
<property name="hibernate.connection.username">postgres</property>
<property name="hibernate.connection.password">postgres</property>
```

### 3. Сборка проекта

```bash
mvn clean compile
```

### 4. Запуск приложения

```bash
mvn exec:java -Dexec.mainClass="com.userservice.Main"
```

Или скомпилируйте и запустите:

```bash
mvn clean package
java -cp target/user-service-1.0-SNAPSHOT.jar:target/classes com.userservice.Main
```

## Использование

После запуска приложения вы увидите меню с опциями:

1. **Создать пользователя (Create)** - создание нового пользователя
2. **Найти пользователя по ID (Read)** - поиск пользователя по идентификатору
3. **Показать всех пользователей (Read All)** - вывод списка всех пользователей
4. **Обновить пользователя (Update)** - обновление данных существующего пользователя
5. **Удалить пользователя (Delete)** - удаление пользователя
6. **Выход** - завершение работы приложения

## Структура проекта

```
user-service/
├── pom.xml
├── README.md
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── userservice/
        │           ├── Main.java              # Главный класс с консольным интерфейсом
        │           ├── dao/
        │           │   └── UserDAO.java       # DAO для работы с пользователями
        │           ├── entity/
        │           │   └── User.java          # Сущность User
        │           └── util/
        │               └── HibernateUtil.java # Утилита для управления Hibernate
        └── resources/
            ├── hibernate.cfg.xml              # Конфигурация Hibernate
            └── log4j2.xml                     # Конфигурация логирования
```

## Особенности

- Использование Hibernate ORM без Spring
- DAO-паттерн для разделения логики работы с БД
- Транзакционность всех операций
- Обработка исключений Hibernate и PostgreSQL
- Логирование операций (log4j2)
- Автоматическое создание таблиц при первом запуске
- Валидация входных данных

## Логи

Логи приложения сохраняются в директории `logs/user-service.log` и также выводятся в консоль.

## Сущность User

Поля сущности User:
- `id` - уникальный идентификатор (автоматически генерируется)
- `name` - имя пользователя (обязательное, до 100 символов)
- `email` - электронная почта (обязательное, уникальное, до 100 символов)
- `age` - возраст (обязательное)
- `created_at` - дата и время создания (автоматически устанавливается)

