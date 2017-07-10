package com.teammerge.services;

import java.util.List;

import com.teammerge.model.BranchModel;
import com.teammerge.model.ExtCommitModel;

public interface BranchService {

  public List<BranchModel> getBranchName(String branchName);

}
