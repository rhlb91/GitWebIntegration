package com.teammerge.dao;


import java.util.List;


import com.teammerge.rest.model.BranchDetailModel;

public interface BranchDetailDao {
 
  public BranchDetailModel getBranchDetails(String branchId);
  public int deleteBranchdao(String branchId);
  public int updateBranchdao(BranchDetailModel branch);
  public int createBranchdao(BranchDetailModel branch);
}