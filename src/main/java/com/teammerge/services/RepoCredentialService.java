package com.teammerge.services;


import com.teammerge.entity.RepoCredentials;

public interface RepoCredentialService {
  
  public RepoCredentials getCredentialDetails(String name);

  public int saveCredential(RepoCredentials name);

}
