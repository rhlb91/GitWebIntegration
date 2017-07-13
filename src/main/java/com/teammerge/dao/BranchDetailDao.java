package com.teammerge.dao;


import org.hibernate.Session;

import com.teammerge.rest.model.BranchDetailModel;

public interface BranchDetailDao{
 
  public BranchDetailModel getBranchDetails(String branchId);
  public void deleteBranchdao(String branchId);
  public void updateBranchdao(BranchDetailModel branch);
  public void createBranchdao(BranchDetailModel branch);
  public Session openCurrentSessionwithTransaction();
  public void closeCurrentSessionwithTransaction();
}