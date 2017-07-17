package com.teammerge.services;


import com.teammerge.entity.RepoCredentials;
import com.teammerge.form.RepoForm;

public interface RepoCredentialService {

  RepoCredentials getCredentialDetails(String name);

  int saveCredential(RepoCredentials name);

  void saveOrUpdateRepoCredentials(final RepoForm repoForm);

}
