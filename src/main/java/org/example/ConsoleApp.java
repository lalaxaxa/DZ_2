package org.example;

import org.example.dao.UserDao;
import org.example.dao.UserDaoImpl;
import org.example.model.User;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class ConsoleApp {
    private static final Logger log = LoggerFactory.getLogger(ConsoleApp.class);
    private final UserDao userDao;
    private final Validator validator;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleApp(UserDao userDao, Validator validator) {
        this.userDao = userDao;
        this.validator = validator;
    }

    public void run() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1":
                        createUser();
                        break;
                    case "2":
                        listUsers();
                        break;
                    case "3":
                        updateUser();
                        break;
                    case "4":
                        deleteUser();
                        break;
                    case "0":
                        System.out.println("Выход.");
                        return;
                    default:
                        System.out.println("Неверный выбор, попробуйте ещё раз.");
                }
            } catch (HibernateException ex) {
                log.error("Ошибка при работе с БД: {}", ex.getMessage(), ex);
                System.out.println("Произошла ошибка при доступе к базе: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                log.warn("Некорректный ввод числа: {}", ex.getMessage());
                System.out.println("Некорректное значение, требуется ввести число");
            } catch (Exception ex) {
                log.error("Непредвиденная ошибка: {}", ex.getMessage(), ex);
                System.out.println("Что-то пошло не так: " + ex.getMessage());
            }
        }
    }

    private void printMenu() {
        System.out.println("\n=== Меню ===");
        System.out.println("1. Создать пользователя");
        System.out.println("2. Показать всех пользователей");
        System.out.println("3. Обновить пользователя");
        System.out.println("4. Удалить пользователя");
        System.out.println("0. Выход");
        System.out.print("Выберите действие: ");
    }

    private void createUser() {
        System.out.print("Имя: ");
        String name = scanner.nextLine();
        System.out.print("Email: ");
        String email = scanner.nextLine();
        System.out.print("Возраст: ");
        int age = Integer.parseInt(scanner.nextLine());
        OffsetDateTime createdAt = OffsetDateTime.now(ZoneOffset.UTC);
        User user = new User(name, email, age, createdAt);
        if (!validateAndPrintErrors(user)) return;
        userDao.create(user);
        System.out.println("Пользователь создан:\n" + user);
    }

    private void listUsers() {
        List<User> users = userDao.findAll();
        if (users.isEmpty()) {
            System.out.println("Нет пользователей.");
        } else {
            System.out.println("Список пользователей:");
            users.forEach(System.out::println);
        }
    }

    private void updateUser() {
        System.out.print("ID пользователя для обновления: ");
        int id = Integer.parseInt(scanner.nextLine());
        User user = userDao.findById(id);
        if (user == null) {
            System.out.println("Пользователь не найден.");
            return;
        } else {
            System.out.println("Пользователь найден:\n" + user);
        }

        String messageTmpl = "Введите новое значение поля '%s' (ENTER - оставить без изменений): ";
        boolean needUpdate = false;

        System.out.print(String.format(messageTmpl, "name"));
        String name = scanner.nextLine();
        if (!name.isBlank()) {
            user.setName(name);
            needUpdate = true;
        }
        System.out.print(String.format(messageTmpl, "email"));
        String email = scanner.nextLine();
        if (!email.isBlank()) {
            user.setEmail(email);
            needUpdate = true;
        }

        System.out.print(String.format(messageTmpl, "age"));
        String ageStr = scanner.nextLine();
        if (!ageStr.isBlank()) {
            int age = Integer.parseInt(ageStr);
            user.setAge(age);
            needUpdate = true;
        }

        if (needUpdate) {
            if (!validateAndPrintErrors(user)) return;
            userDao.update(user);
            System.out.println("Пользователь обновлён:\n" + user);
        } else {
            System.out.println("Ни одно поле не было изменено, обновление не требуется.");
        }

    }

    private void deleteUser() {
        System.out.print("ID пользователя для удаления: ");
        int id = Integer.parseInt(scanner.nextLine());
        boolean isDeleted = userDao.delete(id);
        if (isDeleted) {
            System.out.println("Пользователь удалён.");
        } else {
            System.out.println("Пользователь не найден.");
        }

    }

    private boolean validateAndPrintErrors(User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (violations.isEmpty()) return true;

        System.out.println("Ошибки валидации:");
        violations.forEach(v ->
                System.out.printf(" - %s: %s%n", v.getPropertyPath(), v.getMessage())
        );
        return false;
    }

    public static void main(String[] args) {

        Configuration configuration = new Configuration().addAnnotatedClass(User.class);
        try (
                SessionFactory sf = configuration.buildSessionFactory();
                ValidatorFactory vf = Validation
                        .byDefaultProvider()
                        .configure()
                        .messageInterpolator(new ParameterMessageInterpolator())
                        .buildValidatorFactory();
        ) {
            Validator val = vf.getValidator();
            UserDao dao = new UserDaoImpl(sf);
            new ConsoleApp(dao, val).run();
        } catch (Exception ex) {
            log.error("Не удалось запустить приложение", ex);
            System.err.println("Не удалось запустить приложение: " + ex.getMessage());
        }
    }
}
