package com.teammerge.dao.impl;

import java.io.Serializable;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.teammerge.dao.BaseDao;
import com.teammerge.utils.HibernateUtils;

@Repository("baseDao")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class BaseDaoImpl<T extends Serializable> implements BaseDao<T> {
  private Class<T> clazz;

  private final Logger LOG = LoggerFactory.getLogger(getClass());

  @SuppressWarnings("unchecked")
  @Override
  public T fetchEntity(String entityId) {
    Session session = HibernateUtils.getSessionFactory().openSession();
    T entity = null;
    try {
      entity = (T) session.get(clazz, entityId);

    } finally {
      session.close();
    }
    return entity;
  }

  @Override
  public synchronized void saveEntity(T entity) {
    Session session = HibernateUtils.getSessionFactory().openSession();
    Transaction transaction = null;

    try {
      transaction = session.beginTransaction();
      session.save(entity);
      transaction.commit();
    } catch (HibernateException e) {
      transaction.rollback();
    } finally {
      session.close();
    }
  }

  @Override
  public synchronized void deleteEntity(T entity) {
    HibernateUtils.openCurrentSessionwithTransaction();
    HibernateUtils.getCurrentSession().delete(entity);
    HibernateUtils.closeCurrentSessionwithTransaction();
  }

  @Override
  public synchronized void saveOrUpdateEntity(T entity) {
    HibernateUtils.openCurrentSessionwithTransaction();
    HibernateUtils.getCurrentSession().saveOrUpdate(entity);
    HibernateUtils.closeCurrentSessionwithTransaction();
  }

  /**
   * The below method is only used to fetch all list of Data, based on Model Class name using
   * HibernateUtils.
   */
  @SuppressWarnings("unchecked")
  @Override
  public List<T> fetchAll() {
    final String queryStr = "from " + clazz.getSimpleName();

    HibernateUtils.openCurrentSession();
    Query query = HibernateUtils.getCurrentSession().createQuery(queryStr);
    List<T> result = query.list();
    HibernateUtils.closeCurrentSession();

    return result;
  }

  @Override
  public void setClazz(Class<T> clazz) {
    this.clazz = clazz;
  }

  @Override
  public void saveInSeparateSession(Session s, T entity) {
    s.saveOrUpdate(entity);
  }

  @Override
  public int deleteEntityForField(String fieldName, String fieldValue) {
    final String queryStr =
        "delete from  " + clazz.getSimpleName() + " as b where b." + fieldName + " = :" + fieldName;

    Session session = HibernateUtils.getSessionFactory().openSession();
    Transaction transaction = null;
    int rowsRemoved = 0;

    Query qry = session.createQuery(queryStr);
    qry.setString(fieldName, fieldValue);

    try {
      transaction = session.beginTransaction();
      rowsRemoved = qry.executeUpdate();
      transaction.commit();
    } catch (HibernateException e) {
      transaction.rollback();
      LOG.error("Error removing entity for " + clazz.getSimpleName() + " for project - "
          + fieldName + " for value " + fieldValue);
    } finally {
      session.close();
    }
    return rowsRemoved;
  }

  @Override
  public int deleteEntityForFieldStartsWith(String fieldName, String fieldValue) {
    final String queryStr =
        "delete from  " + clazz.getSimpleName() + " as b where b." + fieldName + " like :"
            + fieldName;

    Session session = HibernateUtils.getSessionFactory().openSession();
    Transaction transaction = null;
    int rowsRemoved = 0;

    Query qry = session.createQuery(queryStr);
    qry.setParameter(fieldName, fieldValue + "%");

    try {
      transaction = session.beginTransaction();
      rowsRemoved = qry.executeUpdate();
      transaction.commit();
    } catch (HibernateException e) {
      transaction.rollback();
      LOG.error("Error removing entity for " + clazz.getSimpleName() + " for project - "
          + fieldName + "fieldValue starts with " + fieldValue);
    } finally {
      session.close();
    }
    return rowsRemoved;
  }

}
