package com.teammerge.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teammerge.dao.BaseDao;
import com.teammerge.dao.RepoCredentialDao;
import com.teammerge.entity.RepoCredentials;
import com.teammerge.entity.RepoCredentialsKey;
import com.teammerge.form.RepoForm;
import com.teammerge.services.RepoCredentialService;

@Service("repoCredentialService")
public class RepoCredentialServiceImpl implements RepoCredentialService {

  @Autowired
  private RepoCredentialDao repoCredentialDao;

  @Override
  public RepoCredentials getCredentialDetails(String name) {
    RepoCredentials repoCredentials = repoCredentialDao.fetchEntity(name);
    return repoCredentials;
  }

  @Override
  public int saveCredential(RepoCredentials repoCredential) {
    repoCredentialDao.saveEntity(repoCredential);
    return 0;
  }

  public void saveOrUpdateRepoCredentials(final RepoForm repoForm) {

    RepoCredentials repoCredentials =
        repoCredentialDao.fetchEntity(repoForm.getCompanyName(), repoForm.getRepoName());

    if (repoCredentials == null) {
      repoCredentials = new RepoCredentials();
      repoCredentials.setCompany(repoForm.getCompanyName());
      repoCredentials.setRepoName(repoForm.getRepoName());
    }

    repoCredentials.setUsername(repoForm.getUsername());
    repoCredentials.setPassword(repoForm.getPassword());

    saveCredential(repoCredentials);
  }
}
