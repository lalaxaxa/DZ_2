package org.example;

import org.example.dao.UserDao;
import org.example.dao.UserDaoImpl;
import org.example.model.User;
import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class ConsoleApp {
    private final UserDao userDao;
    private final Scanner scanner = new Scanner(System.in);

    public ConsoleApp(UserDao userDao) {
        this.userDao = userDao;
    }

    public void run() {
        while (true) {
            printMenu();
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1": createUser(); break;
                    case "2": listUsers(); break;
                    case "3": updateUser(); break;
                    case "4": deleteUser(); break;
                    case "0":
                        System.out.println("Выход.");
                        return;
                    default:
                        System.out.println("Неверный выбор, попробуйте ещё раз.");
                }
            } catch (HibernateException ex) {
                System.out.println("Произошла ошибка при доступе к базе: " + ex.getMessage());
            } catch (NumberFormatException ex){
                System.out.println("Пожалуйста, вводите только цифры там, где требуется ID или возраст.");
            } catch (Exception ex){
                System.out.println("Что‑то пошло не так: " + ex.getMessage());
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
        User user = new User(name, email, age, new Date());
        userDao.create(user);
        System.out.println("Пользователь создан с id=" + user.getId());
    }

    private void listUsers() {
        List<User> users = userDao.findAll();
        if (users.isEmpty()) {
            System.out.println("Нет пользователей.");
        } else {
            System.out.println("Список пользователей:");
            users.forEach(u ->
                    System.out.printf("ID=%d, %s, %s, %d, создан: %s%n",
                            u.getId(), u.getName(), u.getEmail(), u.getAge(), u.getCreatedAt())
            );
        }
    }

    private void updateUser() {
        System.out.print("ID пользователя для обновления: ");
        long id = Long.parseLong(scanner.nextLine());
        User user = userDao.findById(id);
        if (user == null) {
            System.out.println("Пользователь не найден.");
            return;
        }
        System.out.print("Новое имя (ENTER - оставить без изменений): ");
        String name = scanner.nextLine();
        if (!name.isBlank()) {
            user.setName(name);
        }
        // аналогично для email и age...
        userDao.update(user);
        System.out.println("Пользователь обновлён.");
    }

    private void deleteUser() {
        System.out.print("ID пользователя для удаления: ");
        long id = Long.parseLong(scanner.nextLine());
        userDao.delete(id);
        System.out.println("Пользователь удалён.");
    }

    public static void main(String[] args) {
        Configuration configuration = new Configuration().addAnnotatedClass(User.class);
        SessionFactory sf = configuration.buildSessionFactory();
        UserDao dao = new UserDaoImpl(sf);
        new ConsoleApp(dao).run();
        sf.close();
    }
}
