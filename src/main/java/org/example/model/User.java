package org.example.model;

import javax.persistence.*;
import javax.validation.constraints.*;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "Users")
public class User {
    @Id
    @Column(name="id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name="name")
    @NotEmpty(message = "Имя не должно быть пустым")
    @Size(min = 2, max = 100, message = "Длина имени должна быть от {min} до {max} символов")
    private String name;
    @Column(name="email")
    @NotEmpty(message = "Email не должен быть пустым")
    @Size(min = 6, max = 100, message = "Длина email должна быть от {min} до {max} символов")
    @Email(message = "Некорректный email")
    private String email;

    @Column(name="age")
    @Min(value = 0, message = "Минимальное значение для возраста 0")
    @Max(value = 150, message = "Максимальное значение для возраста 150")
    private int age;

    @Column(name="created_at")
    private OffsetDateTime createdAt;

    public User(){}

    public User(String name, String email, int age, OffsetDateTime createdAt) {
        this.name = name;
        this.email = email;
        this.age = age;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        return "id=" + id +
            ", name='" + name + '\'' +
            ", email='" + email + '\'' +
            ", age=" + age +
            ", created_at=" + createdAt
                .atZoneSameInstant(ZoneId.systemDefault())
                .format(fmt);
    }
}


