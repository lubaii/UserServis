package com.userservice.dao;

import com.userservice.entity.User;
import com.userservice.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class UserDAO {
    private static final Logger logger = LogManager.getLogger(UserDAO.class);
    private final SessionFactory sessionFactory;

    public UserDAO() {
        this.sessionFactory = HibernateUtil.getSessionFactory();
    }

    public Long create(User user) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            
            Long id = (Long) session.save(user);
            transaction.commit();
            
            logger.info("User created successfully with ID: {}", id);
            return id;
        } catch (ConstraintViolationException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Constraint violation while creating user: {}", e.getMessage());
            throw new RuntimeException("User with this email already exists", e);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error creating user", e);
            throw new RuntimeException("Failed to create user", e);
        } finally {
            session.close();
        }
    }

    public User read(Long id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            
            User user = session.get(User.class, id);
            transaction.commit();
            
            if (user != null) {
                logger.info("User found with ID: {}", id);
            } else {
                logger.warn("User not found with ID: {}", id);
            }
            return user;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error reading user with ID: {}", id, e);
            throw new RuntimeException("Failed to read user", e);
        } finally {
            session.close();
        }
    }

    public List<User> readAll() {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            
            List<User> users = session.createQuery("FROM User", User.class).list();
            transaction.commit();
            
            logger.info("Retrieved {} users", users.size());
            return users;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error reading all users", e);
            throw new RuntimeException("Failed to read all users", e);
        } finally {
            session.close();
        }
    }

    public void update(User user) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            
            session.update(user);
            transaction.commit();
            
            logger.info("User updated successfully with ID: {}", user.getId());
        } catch (ConstraintViolationException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Constraint violation while updating user: {}", e.getMessage());
            throw new RuntimeException("User with this email already exists", e);
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error updating user with ID: {}", user.getId(), e);
            throw new RuntimeException("Failed to update user", e);
        } finally {
            session.close();
        }
    }

    public void delete(Long id) {
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try {
            transaction = session.beginTransaction();
            
            User user = session.get(User.class, id);
            if (user != null) {
                session.delete(user);
                transaction.commit();
                logger.info("User deleted successfully with ID: {}", id);
            } else {
                transaction.rollback();
                logger.warn("User not found with ID: {}, nothing to delete", id);
                throw new RuntimeException("User with ID " + id + " not found");
            }
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            logger.error("Error deleting user with ID: {}", id, e);
            if (e instanceof RuntimeException) {
                throw e;
            }
            throw new RuntimeException("Failed to delete user", e);
        } finally {
            session.close();
        }
    }
}

