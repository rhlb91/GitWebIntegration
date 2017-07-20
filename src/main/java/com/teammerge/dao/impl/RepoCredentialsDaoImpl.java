package com.teammerge.dao.impl;

import org.springframework.stereotype.Repository;

import com.teammerge.dao.RepoCredentialDao;
import com.teammerge.entity.RepoCredentials;
import com.teammerge.entity.RepoCredentialsKey;
import com.teammerge.utils.HibernateUtils;

@Repository("repoCredentialDao")
public class RepoCredentialsDaoImpl extends BaseDaoImpl<RepoCredentials> implements
    RepoCredentialDao {

  @Override
  public RepoCredentials fetchEntity(RepoCredentialsKey key) {
    HibernateUtils.openCurrentSessionwithTransaction();
    RepoCredentials entity =
        (RepoCredentials) HibernateUtils.getCurrentSession().get(RepoCredentials.class, key);
    HibernateUtils.closeCurrentSessionwithTransaction();
    return entity;
  }

  @Override
  public RepoCredentials fetchEntity(String company, String repoName) {
    RepoCredentialsKey key = new RepoCredentialsKey(company, repoName);
    return fetchEntity(key);
  }

  @Override
  public void setClazz(Class<RepoCredentials> clazz) {
    super.setClazz(RepoCredentials.class);
  }



}
