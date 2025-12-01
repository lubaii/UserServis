package com.userservice.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HibernateUtil {
    private static final Logger logger = LogManager.getLogger(HibernateUtil.class);
    private static SessionFactory sessionFactory;
    private static Exception initializationException;

    public static synchronized SessionFactory getSessionFactory() {
        if (sessionFactory == null && initializationException == null) {
            try {
                // Устанавливаем системные свойства из переменных окружения, если они не заданы
                String dbUrl = System.getProperty("db.url");
                if (dbUrl == null || dbUrl.isEmpty()) {
                    dbUrl = System.getenv("DB_URL");
                    if (dbUrl != null && !dbUrl.isEmpty()) {
                        System.setProperty("db.url", dbUrl);
                    }
                }
                
                String dbUsername = System.getProperty("db.username");
                if (dbUsername == null || dbUsername.isEmpty()) {
                    dbUsername = System.getenv("DB_USERNAME");
                    if (dbUsername != null && !dbUsername.isEmpty()) {
                        System.setProperty("db.username", dbUsername);
                    }
                }
                
                String dbPassword = System.getProperty("db.password");
                if (dbPassword == null || dbPassword.isEmpty()) {
                    dbPassword = System.getenv("DB_PASSWORD");
                    if (dbPassword != null && !dbPassword.isEmpty()) {
                        System.setProperty("db.password", dbPassword);
                    }
                }
                
                StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                        .configure("hibernate.cfg.xml")
                        .build();
                
                sessionFactory = new MetadataSources(registry)
                        .buildMetadata()
                        .buildSessionFactory();
                
                logger.info("Hibernate SessionFactory created successfully");
            } catch (Exception e) {
                logger.error("Initial SessionFactory creation failed", e);
                initializationException = e;
                throw new RuntimeException("Не удалось подключиться к базе данных. " +
                        "Проверьте настройки подключения в hibernate.cfg.xml и убедитесь, что PostgreSQL запущен. " +
                        "Детали: " + e.getMessage(), e);
            }
        }
        
        if (initializationException != null) {
            throw new RuntimeException("Не удалось подключиться к базе данных. " +
                    "Проверьте настройки подключения в hibernate.cfg.xml и убедитесь, что PostgreSQL запущен. " +
                    "Детали: " + initializationException.getMessage(), initializationException);
        }
        
        if (sessionFactory == null) {
            throw new IllegalStateException("SessionFactory is not initialized");
        }
        
        return sessionFactory;
    }

    public static void shutdown() {
        if (sessionFactory != null) {
            sessionFactory.close();
            logger.info("Hibernate SessionFactory closed");
        }
    }
}

