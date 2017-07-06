package com.teammerge.services;

import java.util.List;

import com.teammerge.model.ExtCommitModel;
import com.teammerge.model.RefModel;
import com.teammerge.model.RepositoryCommit;

public interface CommitService {

  public List<ExtCommitModel> getDetailsForBranchName(String branchName);
}
