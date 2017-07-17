package com.teammerge.dao.impl;

import com.teammerge.dao.RepoCredentialDao;
import com.teammerge.entity.RepoCredentials;
import com.teammerge.utils.HibernateUtils;

public class RepoCredentialDaoImpl implements RepoCredentialDao{

  @Override
  public RepoCredentials getCredentials(String name) {
    HibernateUtils.openCurrentSessionwithTransaction();
    RepoCredentials repoCredentials = (RepoCredentials) HibernateUtils.getCurrentSession().get(RepoCredentials.class, name);
    HibernateUtils.closeCurrentSessionwithTransaction();
    return repoCredentials;
  }

  @Override
  public void saveCredentials(RepoCredentials repoCredential) {
    HibernateUtils.openCurrentSessionwithTransaction();
    HibernateUtils.getCurrentSession().save(repoCredential);
    HibernateUtils.closeCurrentSessionwithTransaction();
  }

}
