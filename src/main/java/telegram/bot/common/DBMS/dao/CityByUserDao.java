package telegram.bot.common.DBMS.dao;

import org.hibernate.Session;
import org.hibernate.Transaction;
import telegram.bot.common.DBMS.models.CityByUser;
import telegram.bot.common.DBMS.utils.HibernateSessionFactoryUtil;

import java.util.List;

public class CityByUserDao {
    public CityByUser findCityByUserById(Integer id) {
        return HibernateSessionFactoryUtil.getSessionFactory().openSession().get(CityByUser.class, id);
    }

    public List<CityByUser> findAll() {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        return (List<CityByUser>) session.createQuery("From CityByUser").list();
    }

    public void save(CityByUser cityByUser) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.save(cityByUser);
        tx1.commit();
        session.close();
    }

    public void update(CityByUser cityByUser) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.update(cityByUser);
        tx1.commit();
        session.close();
    }

    public void delete(CityByUser cityByUser) {
        Session session = HibernateSessionFactoryUtil.getSessionFactory().openSession();
        Transaction tx1 = session.beginTransaction();
        session.delete(cityByUser);
        tx1.commit();
        session.close();
    }
}
