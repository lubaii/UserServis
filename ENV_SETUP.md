# Настройка переменных окружения

Для безопасности пароли и другие чувствительные данные должны храниться в переменных окружения, а не в файлах конфигурации.

## Настройка для тестов

Перед запуском интеграционных тестов установите переменные окружения:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/usersdb
export DB_USERNAME=postgres
export DB_PASSWORD=your_actual_password
```

Затем запустите тесты:

```bash
mvn test
```

## Настройка для основного приложения

### Вариант 1: Переменные окружения

Установите переменные окружения:

```bash
export DB_URL=jdbc:postgresql://localhost:5432/usersdb
export DB_USERNAME=postgres
export DB_PASSWORD=your_actual_password
```

Затем запустите приложение с системными свойствами:

```bash
mvn exec:java -Dexec.mainClass="com.userservice.Main" \
  -Ddb.url="${DB_URL}" \
  -Ddb.username="${DB_USERNAME}" \
  -Ddb.password="${DB_PASSWORD}"
```

### Вариант 2: Системные свойства Java

Запустите приложение напрямую с системными свойствами:

```bash
mvn exec:java -Dexec.mainClass="com.userservice.Main" \
  -Ddb.url="jdbc:postgresql://localhost:5432/usersdb" \
  -Ddb.username="postgres" \
  -Ddb.password="your_actual_password"
```

### Вариант 3: Скрипт запуска

Создайте скрипт `run.sh`:

```bash
#!/bin/bash
export DB_URL=jdbc:postgresql://localhost:5432/usersdb
export DB_USERNAME=postgres
export DB_PASSWORD=your_actual_password

mvn exec:java -Dexec.mainClass="com.userservice.Main" \
  -Ddb.url="${DB_URL}" \
  -Ddb.username="${DB_USERNAME}" \
  -Ddb.password="${DB_PASSWORD}"
```

Сделайте скрипт исполняемым:

```bash
chmod +x run.sh
./run.sh
```

## Важно

- **НЕ коммитьте** файлы с паролями в систему контроля версий
- Файл `.env` уже добавлен в `.gitignore`
- Используйте разные пароли для разных окружений (development, test, production)

