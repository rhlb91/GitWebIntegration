package com.teammerge.dao.impl;

import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.teammerge.dao.CompanyDao;
import com.teammerge.entity.Company;
import com.teammerge.utils.HibernateUtils;
import com.teammerge.utils.StringUtils;

@Repository("companyDao")
public class CompanyDaoImpl extends BaseDaoImpl<Company> implements CompanyDao {
  private final String FETCH_COMPANIES = "from Company c";

  @Override
  public void setClazz(Class<Company> clazz) {
    super.setClazz(clazz);
  }

  @Override
  public String getRemoteUrlForProject(String projectId) {
    String remoteUrl = null;
    HibernateUtils.openCurrentSession();
    Query query = HibernateUtils.getCurrentSession().createQuery(FETCH_COMPANIES);
    List<Company> companies = query.list();
    HibernateUtils.openCurrentSession();

    if (CollectionUtils.isEmpty(companies)) {
      return null;
    }

    for (Company c : companies) {
      if (MapUtils.isNotEmpty(c.getRemoteRepoUrls())) {
        remoteUrl = c.getRemoteRepoUrls().get(projectId);
        if (!StringUtils.isEmpty(remoteUrl)) {
          break;
        }
      }
    }
    return remoteUrl;
  }
}
