package com.teammerge.services;

import java.util.List;

import com.teammerge.model.BranchModel;

public interface BranchService {

  public List<BranchModel> getBranchesWithMinimumDetails(String branchName);

  BranchModel getBranchDetails(String branchId);

  int saveBranch(BranchModel branch);

  List<BranchModel> getBranchDetailsForBranchLike(String branchId);

}
