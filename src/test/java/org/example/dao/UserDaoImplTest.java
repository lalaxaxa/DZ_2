package org.example.dao;

import org.example.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDaoImplTest {
    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:13")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    private static SessionFactory sessionFactory;
    private UserDao userDao;

    @BeforeAll
    static void setUpClass() {
        postgres.start();
        Configuration cfg = new Configuration();
        cfg.setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        cfg.setProperty("hibernate.hbm2ddl.auto", "create-drop");
        cfg.setProperty("hibernate.connection.url", postgres.getJdbcUrl());
        cfg.setProperty("hibernate.connection.username", postgres.getUsername());
        cfg.setProperty("hibernate.connection.password", postgres.getPassword());
        cfg.addAnnotatedClass(User.class);
        sessionFactory = cfg.buildSessionFactory();
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDaoImpl(sessionFactory);
    }

    @AfterEach
    void cleanUp() {
        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createQuery("DELETE FROM User").executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    void createAndFindById_shouldPersistAndRetrieve() {
        User user = new User("A1", "A1@test.com", 35, OffsetDateTime.now(ZoneOffset.UTC));
        userDao.create(user);

        User found = userDao.findById(user.getId());
        assertNotNull(found);
        assertEquals("A1@test.com", found.getEmail());
    }

    @Test
    void findAll_shouldReturnAllPersistedUsers() {
        User user1 = new User("A1", "a1@test.com", 31, OffsetDateTime.now(ZoneOffset.UTC));
        User user2 = new User("A2", "a2@test.com", 32, OffsetDateTime.now(ZoneOffset.UTC));
        userDao.create(user1);
        userDao.create(user2);

        List<User> all = userDao.findAll();
        assertEquals(2, all.size());
    }

    @Test
    void update_shouldModifyExistingEntity() {
        User user = new User("Old", "old@test.com", 46, OffsetDateTime.now(ZoneOffset.UTC));
        userDao.create(user);
        user.setName("New");

        userDao.update(user);
        User updated = userDao.findById(user.getId());
        assertEquals("New", updated.getName());
    }

    @Test
    void delete_shouldRemoveEntity() {
        User user = new User("Del", "del@test.com", 27, OffsetDateTime.now(ZoneOffset.UTC));
        userDao.create(user);

        userDao.delete(user);
        User deleted = userDao.findById(user.getId());
        assertNull(deleted);
    }
}