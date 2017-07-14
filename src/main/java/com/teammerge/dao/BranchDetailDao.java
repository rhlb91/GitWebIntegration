package com.teammerge.dao;


import com.teammerge.model.BranchDetailModel;

public interface BranchDetailDao{
 
  public BranchDetailModel getBranchDetails(String branchId);
  public void createBranch(BranchDetailModel branch);
}