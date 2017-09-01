package com.teammerge.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.teammerge.dao.CompanyDao;
import com.teammerge.entity.Company;
import com.teammerge.entity.CompanyKey;
import com.teammerge.utils.HibernateUtils;

@Repository("companyDao")
public class CompanyDaoImpl extends BaseDaoImpl<Company> implements CompanyDao {

  @Override
  public Company fetchEntity(CompanyKey key) {
    HibernateUtils.openCurrentSession();
    Company entity = (Company) HibernateUtils.getCurrentSession().get(Company.class, key);
    HibernateUtils.closeCurrentSession();
    return entity;
  }

  @Override
  public List<Company> fetchEntityForName(String cName) {
    final String queryStr = "from Company where name = :cName";

    HibernateUtils.openCurrentSession();
    Query query = HibernateUtils.getCurrentSession().createQuery(queryStr);
    query.setParameter("cName", cName);
    List<Company> result = query.list();
    HibernateUtils.closeCurrentSession();

    return result;
  }

  @Override
  public Company fetchEntity(String cName, String pName) {
    CompanyKey key = new CompanyKey(cName, pName);
    return fetchEntity(key);
  }

  @Override
  public void setClazz(Class<Company> clazz) {
    super.setClazz(clazz);
  }

}
