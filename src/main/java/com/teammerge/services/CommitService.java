package com.teammerge.services;

import java.util.List;
import java.util.Map;

import com.teammerge.model.ExtCommitModel;

public interface CommitService {

  public Map<String,List<ExtCommitModel>> getDetailsForBranchName(String branchName);
}
