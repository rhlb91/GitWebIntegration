package com.teammerge.dao;

import java.io.Serializable;

public interface BaseDao<T extends Serializable> {

  void setClazz(Class<T> clazz);

  T fetchEntity(String entityId);

  void saveEntity(T entity);
}
