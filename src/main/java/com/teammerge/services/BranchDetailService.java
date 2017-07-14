package com.teammerge.services;

import com.teammerge.model.BranchDetailModel;

public interface BranchDetailService {

  public BranchDetailModel getBranchDetails(String branchId);

  public int createBranch(BranchDetailModel branch);
}
