package com.teammerge.services;

import java.util.List;

import com.teammerge.model.BranchDetailModel;

public interface BranchDetailService {
  public BranchDetailModel getBranchDetailService(String branchId);
  public int deleteBranchDetails(String branchId);
  public int upadateBranchDetails(BranchDetailModel branch);
  public int createBranch(BranchDetailModel branch);
}
