package com.teammerge.services;

import java.util.List;

import com.teammerge.model.ExtCommitModel;

public interface CommitService {

  public List<ExtCommitModel> getDetailsForBranchName(String branchName);

}
