package com.teammerge.dao;

import java.io.Serializable;
import java.util.List;

public interface BaseDao<T extends Serializable> {

  public void setClazz(Class<T> clazz);

  T fetchEntity(String entityId);

  List<T> fetchAll();

  void saveEntity(T entity);

}
