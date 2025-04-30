package org.example;

import org.example.model.User;
import org.example.service.UserService;
import org.hibernate.HibernateException;
import java.util.List;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleApp {
    private static final Logger log = LoggerFactory.getLogger(ConsoleApp.class);
    private final UserService userService;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleApp(UserService userService) {
        this.userService = userService;
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
            } catch (IllegalArgumentException ex) {
                log.warn(ex.getMessage());
                System.out.println(ex.getMessage());
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
        int age = readInt("Возраст: ", false);

        User user = userService.create(name, email, age);
        System.out.println("Пользователь создан: " + user);
        log.info("Создан пользователь: {}", user);
    }

    private void listUsers() {
        List<User> users = userService.findAll();
        if (users.isEmpty()) {
            System.out.println("Нет пользователей.");
        } else {
            System.out.println("Список пользователей:");
            users.forEach(System.out::println);
            System.out.println("Всего пользователей: " + users.size());
        }
        log.info("Выведено пользователей: {}", users.size());
    }

    private void updateUser() {
        int id = readInt("ID пользователя для обновления: ", false);
        User user = userService.findById(id);
        if (user == null) {
            System.out.println("Пользователь с ID=" + id + " не найден!");
            return;
        } else {
            System.out.println("Пользователь найден: " + user);
        }

        boolean needUpdate = false;
        String messageTmpl = "Введите новое значение поля '%s' (ENTER - оставить без изменений): ";

        System.out.print(String.format(messageTmpl, "name"));
        String name = scanner.nextLine();
        if(name.isBlank()) name = null;

        System.out.print(String.format(messageTmpl, "email"));
        String email = scanner.nextLine();
        if(email.isBlank()) email = null;

        Integer age = readInt(String.format(messageTmpl, "age"), true);

        if (name != null || email != null || age != null) needUpdate = true;

        if (needUpdate) {
            User updatedUser = userService.update(id, name, email, age);
            System.out.println("Пользователь обновлён: " + updatedUser);
            log.info("Обновлён пользователь: {}", updatedUser);
        } else {
            System.out.println("Ни одно поле не было изменено, обновление не требуется.");
        }

    }

    private void deleteUser() {
        int id = readInt("ID пользователя для удаления: ", false);
        User deletedUser = userService.delete(id);
        if (deletedUser == null) {
            System.out.println("Пользователь с ID=" + id + " не найден!");
        }
        else {
            System.out.println("Пользователь удалён: " + deletedUser);
            log.info("Удаление пользователя: {}", deletedUser);
        }

    }

    private Integer readInt(String message, boolean blankInputIsOK){
        while (true){
            System.out.print(message);
            String input = scanner.nextLine();
            if (blankInputIsOK && input.isBlank()) return null;
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException ex) {
                System.out.println("Некорректный ввод. Введите целое число.");
            }
        }
    }
}
