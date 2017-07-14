package com.teammerge.services.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.teammerge.dao.BranchDetailDao;
import com.teammerge.model.BranchDetailModel;
import com.teammerge.services.BranchDetailService;

@Service("branchDetailService")
public class BranchDetailServiceImpl implements BranchDetailService {

  @Resource(name = "branchDetailDao")
  private BranchDetailDao branchDetailDao;

  @Override
  public BranchDetailModel getBranchDetails(String branchId) {
    // TODO Auto-generated method stub
    BranchDetailModel branchdetails = branchDetailDao.getBranchDetails(branchId);
    return branchdetails;
  }

  @Override
  public int createBranch(BranchDetailModel branch) {
    // TODO Auto-generated method stub
    branchDetailDao.createBranchdao(branch);
    return 0;
  }
}
