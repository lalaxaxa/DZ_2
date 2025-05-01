package org.example.service;

import org.example.dao.UserDao;
import org.example.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import javax.validation.Path;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserDao userDao;

    @Mock
    private Validator validator;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        // Моки внедряются MockitoExtension
    }

    @Test
    void create_validUser_shouldReturnUserAndInvokeDao() {
        // Arrange
        String name = "Alice";
        String email = "alice@example.com";
        int age = 30;
        when(validator.validate(any(User.class))).thenReturn(Collections.emptySet());
        doNothing().when(userDao).create(any(User.class));

        // Act
        User result = userService.create(name, email, age);

        // Assert
        assertEquals(name, result.getName());
        assertEquals(email, result.getEmail());
        assertEquals(age, result.getAge());
        assertNotNull(result.getCreatedAt(), "createdAt should not be null");
    }

    @Test
    void create_invalidUser_shouldThrowException() {
        // Arrange
        // возвращать из validate() ошибку
        Set<ConstraintViolation<User>> errors = mockViolations("email", "некорректный email");
        when(validator.validate(any(User.class))).thenReturn(errors);
        // Act
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.create("Bob", "Bob-test", 25)

        );
        // Assert
        assertTrue(ex.getMessage().contains("Ошибка валидации"));
        assertTrue(ex.getMessage().contains("email: некорректный email"));
    }


    @Test
    void findAll_shouldReturnListFromDao() {
        // Arrange
        List<User> users = List.of(
                new User("Alice", "Alice@test.com", 20, OffsetDateTime.now(ZoneOffset.UTC)),
                new User("Bob", "Bob@test.com", 30, OffsetDateTime.now(ZoneOffset.UTC))
        );
        when(userDao.findAll()).thenReturn(users);

        // Act
        List<User> result = userService.findAll();

        // Assert
        assertEquals(users, result);
    }

    @Test
    void findById_existingId_shouldReturnUser() {
        // Arrange
        User user = new User("Alice", "Alice@test.com", 22, OffsetDateTime.now(ZoneOffset.UTC));
        when(userDao.findById(1)).thenReturn(user);

        // Act
        User result = userService.findById(1);

        // Assert
        assertEquals(user, result);
    }

    @Test
    void update_existingUser_shouldModifyAndReturn() {
        // Arrange
        User existing = new User("Alice", "Alice@test.com", 28, OffsetDateTime.now(ZoneOffset.UTC));
        existing.setId(2);
        when(userDao.findById(2)).thenReturn(existing);
        when(validator.validate(existing)).thenReturn(Collections.emptySet());
        doNothing().when(userDao).update(existing);

        // Act
        User updated = userService.update(2, "Bob", null, 29);

        // Assert
        assertEquals("Bob", updated.getName());
        assertEquals("Alice@test.com", updated.getEmail());
        assertEquals(29, updated.getAge());
    }

    @Test
    void update_nonExistingUser_shouldThrowException() {
        // Arrange
        when(userDao.findById(99)).thenReturn(null);

        // Act & Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.update(99, "Alice", "Alice@test.com", 40)
        );
        assertTrue(ex.getMessage().contains("Пользователь с ID=99 не найден"));
    }

    @Test
    void update_invalidUser_shouldThrowException() {
        // Arrange
        User existing = new User("Alice", "Alice@test.com", 28, OffsetDateTime.now(ZoneOffset.UTC));
        existing.setId(2);
        when(userDao.findById(2)).thenReturn(existing);
        // возвращать из validate() ошибку
        Set<ConstraintViolation<User>> errors = mockViolations("age", "некорректный age");
        when(validator.validate(any(User.class))).thenReturn(errors);
        // Act
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> userService.update(2, "Bob", null, 190)
        );
        // Assert
        assertTrue(ex.getMessage().contains("Ошибка валидации"));
        assertTrue(ex.getMessage().contains("age: некорректный age"));

    }

    @Test
    void delete_existingUser_shouldInvokeDaoAndReturnUser() {
        // Arrange
        User toDelete = new User("Alice", "Alice@test.com", 35, OffsetDateTime.now(ZoneOffset.UTC));
        when(userDao.findById(3)).thenReturn(toDelete);
        doNothing().when(userDao).delete(toDelete);

        // Act
        User deleted = userService.delete(3);

        // Assert
        assertEquals(toDelete, deleted);
    }

    @Test
    void delete_nonExistingUser_shouldReturnNull() {
        // Arrange
        when(userDao.findById(100)).thenReturn(null);

        // Act
        User deleted = userService.delete(100);

        // Assert
        assertNull(deleted);
    }


    // вернуть из validate() ошибку
    private Set<ConstraintViolation<User>> mockViolations(String property, String message){
        // мок ConstraintViolation
        ConstraintViolation<User> violation = mock(ConstraintViolation.class);
        // мок Path и задаем toString()
        Path path = mock(Path.class);
        //when(path.toString()).thenReturn(property);
        doReturn(property).when(path).toString();
        // возвращаем Path и сообщение ошибки
        when(violation.getPropertyPath()).thenReturn(path);
        when(violation.getMessage()).thenReturn(message);

        Set<ConstraintViolation<User>> errors = new HashSet<>();
        errors.add(violation);
        return errors;
    }
}