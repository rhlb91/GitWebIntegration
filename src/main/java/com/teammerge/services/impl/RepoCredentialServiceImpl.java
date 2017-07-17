package com.teammerge.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teammerge.dao.BaseDao;
import com.teammerge.entity.RepoCredentials;
import com.teammerge.services.RepoCredentialService;

@Service("repoCredentialService")
public class RepoCredentialServiceImpl implements RepoCredentialService {

  private BaseDao<RepoCredentials> baseDao;

  @Override
  public RepoCredentials getCredentialDetails(String name) {
    RepoCredentials repoCredentials = baseDao.fetchEntity(name);
    return repoCredentials;
  }

  @Override
  public int saveCredential(RepoCredentials repoCredential) {
    baseDao.saveEntity(repoCredential);
    return 0;
  }


  @Autowired
  public void setBaseDao(BaseDao<RepoCredentials> baseDao) {
    baseDao.setClazz(RepoCredentials.class);
    this.baseDao = baseDao;
  }
}
