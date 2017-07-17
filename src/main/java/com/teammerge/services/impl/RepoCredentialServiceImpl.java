package com.teammerge.services.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.teammerge.dao.CompanyDetailDao;
import com.teammerge.dao.RepoCredentialDao;
import com.teammerge.entity.RepoCredentials;
import com.teammerge.services.RepoCredentialService;

@Service("repoCredentialService")
public class RepoCredentialServiceImpl implements RepoCredentialService {

  @Resource(name = "repoCredentialDao")
  private RepoCredentialDao repoCredentialDao;
  
  @Override
  public RepoCredentials getCredentialDetails(String name) {
   RepoCredentials repoCredentials=repoCredentialDao.getCredentials(name);
    return repoCredentials;
  }

  @Override
  public int saveCredential(RepoCredentials repoCredential) {
    repoCredentialDao.saveCredentials(repoCredential);
    return 0;
  }

}
