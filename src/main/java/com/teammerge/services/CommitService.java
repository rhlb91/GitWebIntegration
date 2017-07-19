package com.teammerge.services;

import java.util.List;
import java.util.Map;

import com.teammerge.form.CommitForm;
import com.teammerge.form.RepoForm;
import com.teammerge.model.CommitModel;

public interface CommitService {

  public Map<String, List<CommitModel>> getDetailsForBranchName(String branchName);
  
  public CommitModel getBranchesbyCommit(String commitId);
  void saveCommit(CommitModel commit);
  void saveOrUpdateCommitDetails(final CommitForm commitForm);
}
