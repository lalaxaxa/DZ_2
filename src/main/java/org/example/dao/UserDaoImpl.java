package org.example.dao;

import org.example.model.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.util.List;

public class UserDaoImpl implements UserDao {
    private final SessionFactory sessionFactory;

    public UserDaoImpl(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }
    @Override
    public void create(User user){
        Transaction tx = null;
        try(Session session = sessionFactory.openSession()){
            tx = session.beginTransaction();
            session.save(user);
            tx.commit();
        }catch (HibernateException ex){
            if (tx != null) tx.rollback();
            throw ex;
        }
    }
    @Override
    public List<User> findAll(){
        try(Session session = sessionFactory.openSession()) {
            return session.createQuery("FROM User", User.class).getResultList();
        }catch (HibernateException ex){
            throw ex;
        }
    }
    @Override
    public User findById(int id){
        try(Session session = sessionFactory.openSession()) {
            return session.get(User.class, id);
        }catch (HibernateException ex){
            throw ex;
        }
    }
    @Override
    public void update(User user){
        Transaction tx = null;
        try(Session session = sessionFactory.openSession()){
            tx = session.beginTransaction();
            session.update(user);
            tx.commit();
        }catch (HibernateException ex){
            if (tx != null) tx.rollback();
            throw ex;
        }
    }
    @Override
    public boolean delete(int id){
        boolean isDelete = false;
        Transaction tx = null;
        try(Session session = sessionFactory.openSession()){
            tx = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null){
                session.delete(user);
                isDelete = true;
            }
            tx.commit();
            return isDelete;
        }catch (HibernateException ex){
            if (tx != null) tx.rollback();
            throw ex;
        }

    }


}
