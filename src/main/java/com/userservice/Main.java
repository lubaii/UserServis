package com.userservice;

import java.util.List;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.userservice.dao.UserDAO;
import com.userservice.entity.User;
import com.userservice.util.HibernateUtil;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class);
    private static final Scanner scanner = new Scanner(System.in);
    private static UserDAO userDAO;

    public static void main(String[] args) {
        logger.info("Starting User Service application");
        
        try {
            // Проверка подключения к БД
            HibernateUtil.getSessionFactory();
            logger.info("Database connection established");
            
            // Инициализация DAO только после успешного подключения
            userDAO = new UserDAO();
            
            showMenu();
            
            boolean running = true;
            while (running) {
                System.out.print("\nВыберите операцию (1-6): ");
                String choice = scanner.nextLine().trim();
                
                try {
                    switch (choice) {
                        case "1":
                            createUser();
                            break;
                        case "2":
                            readUser();
                            break;
                        case "3":
                            readAllUsers();
                            break;
                        case "4":
                            updateUser();
                            break;
                        case "5":
                            deleteUser();
                            break;
                        case "6":
                            running = false;
                            System.out.println("Выход из приложения...");
                            break;
                        default:
                            System.out.println("Неверный выбор. Пожалуйста, выберите от 1 до 6.");
                    }
                } catch (Exception e) {
                    System.out.println("Ошибка: " + e.getMessage());
                    logger.error("Error during operation", e);
                }
            }
        } catch (Exception e) {
            logger.error("Fatal error in application", e);
            System.err.println("\n=== КРИТИЧЕСКАЯ ОШИБКА ===");
            System.err.println(e.getMessage());
            
            Throwable cause = e.getCause();
            if (cause != null && cause.getMessage() != null) {
                if (cause.getMessage().contains("password authentication failed")) {
                    System.err.println("\nОшибка аутентификации PostgreSQL!");
                    System.err.println("Проверьте правильность пароля в файле hibernate.cfg.xml");
                } else if (cause.getMessage().contains("Connection refused") || cause.getMessage().contains("could not connect")) {
                    System.err.println("\nНе удалось подключиться к PostgreSQL!");
                    System.err.println("Убедитесь, что PostgreSQL запущен и доступен на localhost:5432");
                } else if (cause.getMessage().contains("database") && cause.getMessage().contains("does not exist")) {
                    System.err.println("\nБаза данных не существует!");
                    System.err.println("Создайте базу данных: CREATE DATABASE usersdb;");
                }
            }
            
            System.err.println("\nПроверьте настройки в файле: src/main/resources/hibernate.cfg.xml");
        } finally {
            scanner.close();
            HibernateUtil.shutdown();
            logger.info("Application shutdown");
        }
    }

    private static void showMenu() {
        System.out.println("\n=== User Service - CRUD Operations ===");
        System.out.println("1. Создать пользователя (Create)");
        System.out.println("2. Найти пользователя по ID (Read)");
        System.out.println("3. Показать всех пользователей (Read All)");
        System.out.println("4. Обновить пользователя (Update)");
        System.out.println("5. Удалить пользователя (Delete)");
        System.out.println("6. Выход");
    }

    private static void createUser() {
        System.out.println("\n--- Создание нового пользователя ---");
        
        System.out.print("Введите имя: ");
        String name = scanner.nextLine().trim();
        if (name.isEmpty()) {
            System.out.println("Имя не может быть пустым!");
            return;
        }
        
        System.out.print("Введите email: ");
        String email = scanner.nextLine().trim();
        if (email.isEmpty()) {
            System.out.println("Email не может быть пустым!");
            return;
        }
        
        System.out.print("Введите возраст: ");
        String ageStr = scanner.nextLine().trim();
        Integer age;
        try {
            age = Integer.parseInt(ageStr);
            if (age < 0 || age > 150) {
                System.out.println("Возраст должен быть от 0 до 150!");
                return;
            }
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат возраста!");
            return;
        }
        
        User user = new User(name, email, age);
        try {
            Long id = userDAO.create(user);
            System.out.println("Пользователь успешно создан с ID: " + id);
        } catch (RuntimeException e) {
            System.out.println("Ошибка при создании пользователя: " + e.getMessage());
        }
    }

    private static void readUser() {
        System.out.println("\n--- Поиск пользователя по ID ---");
        
        System.out.print("Введите ID пользователя: ");
        String idStr = scanner.nextLine().trim();
        Long id;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат ID!");
            return;
        }
        
        User user = userDAO.read(id);
        if (user != null) {
            System.out.println("\nНайден пользователь:");
            System.out.println("ID: " + user.getId());
            System.out.println("Имя: " + user.getName());
            System.out.println("Email: " + user.getEmail());
            System.out.println("Возраст: " + user.getAge());
            System.out.println("Дата создания: " + user.getCreatedAt());
        } else {
            System.out.println("Пользователь с ID " + id + " не найден.");
        }
    }

    private static void readAllUsers() {
        System.out.println("\n--- Список всех пользователей ---");
        
        List<User> users = userDAO.readAll();
        if (users.isEmpty()) {
            System.out.println("Пользователи не найдены.");
        } else {
            System.out.println("\nВсего пользователей: " + users.size());
            System.out.println("----------------------------------------");
            for (User user : users) {
                System.out.println("ID: " + user.getId() + 
                                 " | Имя: " + user.getName() + 
                                 " | Email: " + user.getEmail() + 
                                 " | Возраст: " + user.getAge() +
                                 " | Создан: " + user.getCreatedAt());
            }
        }
    }

    private static void updateUser() {
        System.out.println("\n--- Обновление пользователя ---");
        
        System.out.print("Введите ID пользователя для обновления: ");
        String idStr = scanner.nextLine().trim();
        Long id;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат ID!");
            return;
        }
        
        User user = userDAO.read(id);
        if (user == null) {
            System.out.println("Пользователь с ID " + id + " не найден.");
            return;
        }
        
        System.out.println("\nТекущие данные пользователя:");
        System.out.println("Имя: " + user.getName());
        System.out.println("Email: " + user.getEmail());
        System.out.println("Возраст: " + user.getAge());
        
        System.out.print("\nВведите новое имя (или нажмите Enter для сохранения текущего): ");
        String name = scanner.nextLine().trim();
        if (!name.isEmpty()) {
            user.setName(name);
        }
        
        System.out.print("Введите новый email (или нажмите Enter для сохранения текущего): ");
        String email = scanner.nextLine().trim();
        if (!email.isEmpty()) {
            user.setEmail(email);
        }
        
        System.out.print("Введите новый возраст (или нажмите Enter для сохранения текущего): ");
        String ageStr = scanner.nextLine().trim();
        if (!ageStr.isEmpty()) {
            try {
                Integer age = Integer.parseInt(ageStr);
                if (age < 0 || age > 150) {
                    System.out.println("Возраст должен быть от 0 до 150!");
                    return;
                }
                user.setAge(age);
            } catch (NumberFormatException e) {
                System.out.println("Неверный формат возраста!");
                return;
            }
        }
        
        try {
            userDAO.update(user);
            System.out.println("Пользователь успешно обновлен!");
        } catch (RuntimeException e) {
            System.out.println("Ошибка при обновлении пользователя: " + e.getMessage());
        }
    }

    private static void deleteUser() {
        System.out.println("\n--- Удаление пользователя ---");
        
        System.out.print("Введите ID пользователя для удаления: ");
        String idStr = scanner.nextLine().trim();
        Long id;
        try {
            id = Long.parseLong(idStr);
        } catch (NumberFormatException e) {
            System.out.println("Неверный формат ID!");
            return;
        }
        
        User user = userDAO.read(id);
        if (user == null) {
            System.out.println("Пользователь с ID " + id + " не найден.");
            return;
        }
        
        System.out.println("\nВы собираетесь удалить пользователя:");
        System.out.println("ID: " + user.getId());
        System.out.println("Имя: " + user.getName());
        System.out.println("Email: " + user.getEmail());
        System.out.print("\nПодтвердите удаление (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();
        
        if ("yes".equals(confirmation) || "y".equals(confirmation)) {
            try {
                userDAO.delete(id);
                System.out.println("Пользователь успешно удален!");
            } catch (RuntimeException e) {
                System.out.println("Ошибка при удалении пользователя: " + e.getMessage());
            }
        } else {
            System.out.println("Удаление отменено.");
        }
    }
}

