package com.teammerge.services;

import java.util.List;
import java.util.Map;

import com.teammerge.model.CommitModel;

public interface CommitService {

  public Map<String, List<CommitModel>> getDetailsForBranchName(String branchName);
}
