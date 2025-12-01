package com.userservice.util;

import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Properties;

public class TestHibernateUtil {
    private static final Logger logger = LogManager.getLogger(TestHibernateUtil.class);
    private static SessionFactory sessionFactory;

    public static SessionFactory createSessionFactory(String jdbcUrl, String username, String password) {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
        
        try {
            Properties properties = new Properties();
            properties.setProperty("hibernate.connection.driver_class", "org.postgresql.Driver");
            properties.setProperty("hibernate.connection.url", jdbcUrl);
            properties.setProperty("hibernate.connection.username", username);
            properties.setProperty("hibernate.connection.password", password);
            properties.setProperty("hibernate.connection.pool_size", "5");
            properties.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
            properties.setProperty("hibernate.current_session_context_class", "thread");
            properties.setProperty("hibernate.show_sql", "false");
            properties.setProperty("hibernate.format_sql", "false");
            properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");

            StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                    .applySettings(properties)
                    .build();
            
            sessionFactory = new MetadataSources(registry)
                    .addAnnotatedClass(com.userservice.entity.User.class)
                    .buildMetadata()
                    .buildSessionFactory();
            
            logger.info("Test Hibernate SessionFactory created successfully");
            return sessionFactory;
        } catch (Exception e) {
            logger.error("Initial Test SessionFactory creation failed", e);
            throw new ExceptionInInitializerError(e);
        }
    }

    public static void shutdown() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
            sessionFactory = null;
            logger.info("Test Hibernate SessionFactory closed");
        }
    }
}

