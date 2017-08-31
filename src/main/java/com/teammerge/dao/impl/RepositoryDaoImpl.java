package com.teammerge.dao.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.teammerge.dao.RepositoryDao;
import com.teammerge.entity.Company;
import com.teammerge.model.RepositoryModel;
import com.teammerge.utils.HibernateUtils;

@Repository("repositoryDao")
public class RepositoryDaoImpl extends BaseDaoImpl<RepositoryModel> implements RepositoryDao {

  private final String FETCH_COMPANY = "from Company c";

  @Override
  public List<RepositoryModel> fetchRepositoriesForCompany(String companyName) {
    return null;
  }

  @Override
  public List<RepositoryModel> fetchAllRepositories() {
    return fetchAll();
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<String> fetchAllRepositoryNames() {
    HibernateUtils.openCurrentSessionwithTransaction();
    Query query = HibernateUtils.getCurrentSession().createQuery(FETCH_COMPANY);
    List<Company> companies = query.list();

    if (CollectionUtils.isEmpty(companies)) {
      return null;
    }

    List<String> repos = new ArrayList<>();
    for (Company company : companies) {
      repos.add(company.getProjectName());
    }

    HibernateUtils.closeCurrentSessionwithTransaction();
    return repos;
  }
}
