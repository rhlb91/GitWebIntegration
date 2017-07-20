package com.teammerge.dao.impl;

import java.io.Serializable;
import java.util.List;

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
    HibernateUtils.openCurrentSessionwithTransaction();
    T entity = (T) HibernateUtils.getCurrentSession().get(clazz, entityId);
    HibernateUtils.closeCurrentSessionwithTransaction();
    return entity;
  }

  @Override
  public void saveEntity(T entity) {
    HibernateUtils.openCurrentSessionwithTransaction();
    HibernateUtils.getCurrentSession().saveOrUpdate(entity);
    HibernateUtils.closeCurrentSessionwithTransaction();
  }
  
/**
 * The below method is only used to fetch all list of Data, based on Model Class name using HibernateUtils.
 */
  @Override
  public List<T> getEntityList(){
    HibernateUtils.openCurrentSessionwithTransaction();
    List<T> Tlist = HibernateUtils.getCurrentSession().createCriteria(clazz).list();   
    HibernateUtils.closeCurrentSessionwithTransaction();
    return Tlist;
    
  }

  @Override
  public void setClazz(Class<T> clazz) {
    this.clazz = clazz;
  }
}
