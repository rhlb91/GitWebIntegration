package com.teammerge.utils;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.stat.Statistics;

public class HibernateUtils {
	
	

  private static Session currentSession;

  private static Transaction currentTransaction;

  private static SessionFactory sessionFactory = null;

  private static Statistics stats = null;
 
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
    stats = sessionFactory.getStatistics();
    stats.setStatisticsEnabled(true);
    return currentSession;
  }

  public static Session openCurrentSessionwithTransaction() {
    currentSession = openCurrentSession();
    currentTransaction = currentSession.beginTransaction();
    return currentSession;
  }

  public static void closeCurrentSession() {
    printStats(stats);
    currentSession.close();
    stats=null;
  }

  public static void closeCurrentSessionwithTransaction() {
    printStats(stats);
    currentTransaction.commit();
    currentSession.close();
    stats=null;
  }

  /**
   * Caution do not use this method alone, use either HibernateUtils.openCurrentSession() or
   * HibernateUtils.openCurrentSessionwithTransaction() before calling this method
   * 
   * @return
   */
  public static Session getCurrentSession() {
	  System.out.println("Enter in HU for getCurrentSession");
    return currentSession;
  }

  private static void printStats(Statistics stats) {
    if (stats != null) {
      System.out.println("Fetch Count=" + stats.getEntityFetchCount());
      System.out.println("Second Level Hit Count=" + stats.getSecondLevelCacheHitCount());
      System.out.println("Second Level Miss Count=" + stats.getSecondLevelCacheMissCount());
      System.out.println("Second Level Put Count=" + stats.getSecondLevelCachePutCount());
      stats.logSummary();
    } else {
      System.out.println("HibernateUtils::Stats is null");
    }
  }

}
