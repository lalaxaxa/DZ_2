package org.example.service;

import org.example.dao.UserDao;
import org.example.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Set;

public class UserServiceImpl implements UserService{
    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);
    private  final UserDao userDao;
    private  final Validator validator;

    public UserServiceImpl(UserDao userDao, Validator validator) {
        this.userDao = userDao;
        this.validator = validator;
    }

    @Override
    public User create(String name, String email, int age) {
        log.debug("-> create(name='{}', email='{}', age={})", name, email, age);
        User user = new User(name, email, age, OffsetDateTime.now(ZoneOffset.UTC));
        validate(user);
        userDao.create(user);
        return user;
    }

    @Override
    public List<User> findAll() {
        log.debug("-> findAll()");
        return userDao.findAll();
    }

    @Override
    public User findById(int id) {
        log.debug("-> findById(name='{}')", id);
        return userDao.findById(id);
    }

    @Override
    public User update(int id, String newName, String newEmail, Integer newAge) {
        log.debug("-> update(id='{}', name='{}', email='{}', age={})", id, newName, newEmail, newAge);
        User user = userDao.findById(id);
        if (user == null) throw new IllegalArgumentException("Пользователь с ID=" + id + " не найден!");
        if (newName != null) user.setName(newName);
        if (newEmail != null) user.setEmail(newEmail);
        if (newAge != null) user.setAge(newAge);
        validate(user);
        userDao.update(user);
        return user;
    }

    @Override
    public User delete(int id) {
        log.debug("-> delete(name='{}')", id);
        User user = userDao.findById(id);
        if (user != null) userDao.delete(user);
        return user;
    }

    private void validate(User user) {
        log.debug("-> validate(user='{}')", user);
        Set<ConstraintViolation<User>> errors = validator.validate(user);
        if (!errors.isEmpty()) {
            StringBuilder sb = new StringBuilder("Ошибка валидации:");
            errors.forEach(v ->
                sb.append(String.format("%n - %s: %s",
                    v.getPropertyPath(),
                    v.getMessage()
                ))
            );
            throw new IllegalArgumentException(sb.toString());
        }
    }
}
