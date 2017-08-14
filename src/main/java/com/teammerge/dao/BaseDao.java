package com.teammerge.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Session;

public interface BaseDao<T extends Serializable> {

  public void setClazz(Class<T> clazz);

  T fetchEntity(String entityId);

  List<T> fetchAll();

  void saveEntity(T entity);

  void saveOrUpdateEntity(T entity);

  void saveInSeparateSession(Session s, T entity);

}
