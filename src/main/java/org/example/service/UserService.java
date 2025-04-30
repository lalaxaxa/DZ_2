package org.example.service;

import org.example.model.User;
import java.util.List;

public interface UserService {
    User create(String name, String email, int age);
    List<User> findAll();
    User findById(int id);
    User update(int id, String name, String email, Integer age);
    User delete(int id);
}