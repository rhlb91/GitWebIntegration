package com.teammerge.dao;

import com.teammerge.entity.RepoCredentials;

public interface RepoCredentialDao {
  
public RepoCredentials getCredentials(String name);
  
public void saveCredentials(RepoCredentials repoCredential);

}
