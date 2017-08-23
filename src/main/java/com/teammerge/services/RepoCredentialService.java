package com.teammerge.services;


import com.teammerge.entity.RepoCredentials;
import com.teammerge.form.CredentialRequestForm;
import com.teammerge.form.RepoForm;

public interface RepoCredentialService {

  int saveCredential(RepoCredentials name);

  void saveOrUpdateRepoCredentials(final RepoForm repoForm);

  RepoCredentials getCredentialDetails(CredentialRequestForm crf);

}
