package com.teammerge.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

public class HibernateUtils {

  private static Session currentSession;

  private static Transaction currentTransaction;

  private static SessionFactory sessionFactory = null;

  public static SessionFactory getSessionFactory() {
    if (sessionFactory == null) {
      Configuration configuration = new Configuration().configure();
      StandardServiceRegistryBuilder builder =
          new StandardServiceRegistryBuilder().applySettings(configuration.getProperties());
      sessionFactory = configuration.buildSessionFactory(builder.build());
    }
    return sessionFactory;
  }

  public static Session openCurrentSession() {
    currentSession = getSessionFactory().openSession();
    return currentSession;
  }

  public static Session openCurrentSessionwithTransaction() {
    currentSession = openCurrentSession();
    currentTransaction = currentSession.beginTransaction();
    return currentSession;
  }

  public static void closeCurrentSession() {
    currentSession.close();
  }

  public static void closeCurrentSessionwithTransaction() {
    currentTransaction.commit();
    currentSession.close();
  }

  /**
   * Caution do not use this method alone, use either HibernateUtils.openCurrentSession() or
   * HibernateUtils.openCurrentSessionwithTransaction() before calling this method
   * 
   * @return
   */
  public static Session getCurrentSession() {
    return currentSession;
  }

}
