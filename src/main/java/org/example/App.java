package org.example;

import org.example.model.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.util.Date;
import java.util.List;

public class App
{
    public static void main( String[] args )
    {
        Configuration configuration = new Configuration().addAnnotatedClass(User.class);

        SessionFactory sessionFactory = configuration.buildSessionFactory();
        Session session = sessionFactory.getCurrentSession();

        try {
            session.beginTransaction();
            //read
            //User user = session.get(User.class, 1);

            //write
            User user1 = new User("Test1", "email1", 1, new Date());
            User user2 = new User("Test2", "email2", 2, new Date());
            User user3 = new User("Test3", "email3", 3, new Date());
            session.save(user1);
            /*session.save(user2);
            session.save(user3);
            //get id new record
            System.out.println(user1.getId());*/

            //update
            /*User user = session.get(User.class, 2);
            user.setName("New Test");*/

            //delete
            /*User user = session.get(User.class, 2);
            session.delete(user);*/

            //use HQL
            /*List<User> people = session.createQuery("FROM User").getResultList();
            List<User> people = session.createQuery("FROM User").getResultList();
            List<User> people = session.createQuery("FROM User where age > 1").getResultList();
            session.createQuery("update  User set name='Test1' where age < 2").executeUpdate();
            session.createQuery("delete User where age < 2").executeUpdate();*/
            List<User> users = session.createQuery("FROM User").getResultList();
            for(User p: users){
                System.out.println(p);
            }

            session.getTransaction().commit();
        } finally {
            sessionFactory.close();
        }

    }
}
