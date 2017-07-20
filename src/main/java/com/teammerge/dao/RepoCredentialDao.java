package com.teammerge.dao;

import com.teammerge.entity.RepoCredentials;
import com.teammerge.entity.RepoCredentialsKey;

public interface RepoCredentialDao extends BaseDao<RepoCredentials> {

  RepoCredentials fetchEntity(RepoCredentialsKey key);
  RepoCredentials fetchEntity(String company,String repoName);
}
