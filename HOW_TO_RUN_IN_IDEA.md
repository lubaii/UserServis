# Как запустить приложение через IntelliJ IDEA

## Способ 1: Запуск через главный класс (Main.java)

### Шаги:

1. **Откройте проект в IntelliJ IDEA**
   - File → Open → выберите папку проекта `UserServis`

2. **Убедитесь, что Maven импортирован**
   - IDEA автоматически обнаружит `pom.xml` и предложит импортировать зависимости
   - Если нет, нажмите правой кнопкой на `pom.xml` → Maven → Reload project

3. **Настройте переменные окружения (важно!)**
   
   **Вариант А: Через Run Configuration**
   - Откройте `src/main/java/com/userservice/Main.java`
   - Нажмите правой кнопкой на класс `Main` → Run → Edit Configurations...
   - В поле "Environment variables" добавьте:
     ```
     DB_URL=jdbc:postgresql://localhost:5432/usersdb
     DB_USERNAME=postgres
     DB_PASSWORD=your_password
     ```
   - Или используйте файл `SETUP_ENV.sh`:
     - Run → Edit Configurations...
     - В "Environment variables" нажмите на иконку папки
     - Выберите "Load from file" и укажите `SETUP_ENV.sh`
   
   **Вариант Б: Через системные переменные**
   - Run → Edit Configurations...
   - В "VM options" добавьте:
     ```
     -DDB_URL=jdbc:postgresql://localhost:5432/usersdb
     -DDB_USERNAME=postgres
     -DDB_PASSWORD=your_password
     ```

4. **Запустите приложение**
   - Нажмите правой кнопкой на класс `Main` → Run 'Main.main()'
   - Или используйте зеленую стрелку рядом с методом `main()`
   - Или нажмите `Shift + F10`

5. **Проверьте запуск**
   - В консоли IDEA вы увидите логи Spring Boot
   - Приложение запустится на порту `8080` (если `app.console=false`)
   - Или откроется консольное меню (если `app.console=true`)

## Способ 2: Запуск через Spring Boot конфигурацию

1. **Создайте Spring Boot конфигурацию**
   - Run → Edit Configurations...
   - Нажмите "+" → Spring Boot
   - В поле "Main class" укажите: `com.userservice.Main`
   - В "Environment variables" добавьте переменные (см. выше)

2. **Запустите**
   - Выберите созданную конфигурацию и нажмите Run

## Способ 3: Запуск через Maven (встроенный в IDEA)

1. **Откройте Maven панель**
   - View → Tool Windows → Maven
   - Или нажмите на вкладку "Maven" справа

2. **Найдите задачу spring-boot:run**
   - Раскройте: `UserServis` → `Plugins` → `spring-boot` → `spring-boot:run`

3. **Настройте переменные окружения**
   - Правой кнопкой на `spring-boot:run` → Modify Run Configuration...
   - В "Environment variables" добавьте переменные (см. выше)

4. **Запустите**
   - Двойной клик на `spring-boot:run`

## Проверка работы приложения

### Если запущен REST API (app.console=false):
```bash
# Проверка здоровья
curl http://localhost:8080/actuator/health

# Создание пользователя
curl -X POST http://localhost:8080/api/users \
  -H "Content-Type: application/json" \
  -d '{"name":"John Doe","email":"john@example.com","age":30}'

# Получение всех пользователей
curl http://localhost:8080/api/users
```

### Если запущен консольный режим (app.console=true):
- В консоли IDEA появится меню с опциями 1-6
- Следуйте инструкциям в меню

## Настройка для отладки (Debug)

1. **Установите breakpoints** в нужных местах кода

2. **Запустите в режиме Debug**
   - Правой кнопкой на `Main` → Debug 'Main.main()'
   - Или нажмите `Shift + F9`

3. **Используйте Debug панель**
   - View → Tool Windows → Debug
   - Доступны: Step Over (F8), Step Into (F7), Resume (F9)

## Решение проблем

### Ошибка: "password authentication failed"
- Проверьте правильность пароля в переменных окружения
- Убедитесь, что PostgreSQL запущен

### Ошибка: "Connection refused"
- Убедитесь, что PostgreSQL запущен: `sudo systemctl status postgresql`
- Проверьте порт: по умолчанию `5432`

### Ошибка: "database does not exist"
- Создайте базу данных:
  ```sql
  CREATE DATABASE usersdb;
  ```

### Порт 8080 занят
- Остановите другое приложение на порту 8080
- Или измените порт в `application.properties`: `server.port=8081`

### IDEA не видит Spring Boot
- File → Invalidate Caches / Restart...
- Убедитесь, что в `pom.xml` есть зависимость `spring-boot-starter`

## Полезные горячие клавиши

- `Ctrl + Shift + F10` - Запустить текущий класс
- `Shift + F10` - Run
- `Shift + F9` - Debug
- `Ctrl + F2` - Остановить выполнение
- `Alt + Enter` - Быстрые исправления

## Дополнительная информация

- Конфигурация приложения: `src/main/resources/application.properties`
- Документация API: `API_DOCUMENTATION.md`
- Инструкции по тестированию: `HOW_TO_TEST.md`

