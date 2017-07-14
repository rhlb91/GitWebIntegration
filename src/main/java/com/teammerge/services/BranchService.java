package com.teammerge.services;

import java.util.List;

import com.teammerge.model.BranchModel;

public interface BranchService {

  public List<BranchModel> getBranchName(String branchName);

}
