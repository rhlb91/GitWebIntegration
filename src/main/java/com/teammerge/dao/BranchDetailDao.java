package com.teammerge.dao;


import org.hibernate.Session;

import com.teammerge.model.BranchDetailModel;

public interface BranchDetailDao{
 
  public BranchDetailModel getBranchDetails(String branchId);
  public void createBranchdao(BranchDetailModel branch);
  public Session openCurrentSessionwithTransaction();
  public void closeCurrentSessionwithTransaction();
}