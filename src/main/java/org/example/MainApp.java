package org.example;

import org.example.dao.UserDao;
import org.example.dao.UserDaoImpl;
import org.example.model.User;
import org.example.service.UserService;
import org.example.service.UserServiceImpl;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.validator.messageinterpolation.ParameterMessageInterpolator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

public class MainApp {
    private static final Logger log = LoggerFactory.getLogger(MainApp.class);
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
            UserDao dao = new UserDaoImpl(sf);
            Validator val = vf.getValidator();
            UserService userService = new UserServiceImpl(dao,val);
            new ConsoleApp(userService).run();
        } catch (Exception ex) {
            log.error("Не удалось запустить приложение", ex);
            System.err.println("Не удалось запустить приложение: " + ex.getMessage());
        }
    }
}
