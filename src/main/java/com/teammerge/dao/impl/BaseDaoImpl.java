package com.teammerge.dao.impl;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.teammerge.dao.BaseDao;
import com.teammerge.utils.HibernateUtils;

@Repository("baseDao")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class BaseDaoImpl<T extends Serializable> implements BaseDao<T> {
  private Class<T> clazz;


  @Override
  public T fetchEntity(String entityId) {
    HibernateUtils.openCurrentSession();
    T entity = (T) HibernateUtils.getCurrentSession().get(clazz, entityId);
    HibernateUtils.closeCurrentSession();
    return entity;
  }

  @Override
  public synchronized void saveEntity(T entity) {
    HibernateUtils.openCurrentSessionwithTransaction();
    HibernateUtils.getCurrentSession().save(entity);
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
  public void saveCommitInSeparateSession(Session s, T entity) {
    s.saveOrUpdate(entity);
  }


}
