package org.example.model;

import javax.persistence.*;
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
    private String name;
    @Column(name="email")
    private String email;
    @Column(name="age")
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


