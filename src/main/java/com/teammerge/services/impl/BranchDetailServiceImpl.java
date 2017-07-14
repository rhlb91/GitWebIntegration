package com.teammerge.services.impl;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.ws.rs.PathParam;

import org.springframework.stereotype.Service;

import com.teammerge.dao.BranchDetailDao;
import com.teammerge.model.BranchDetailModel;
import com.teammerge.services.BranchDetailService;

@Service("branchDetailService")
public class BranchDetailServiceImpl implements BranchDetailService {

  @Resource(name = "branchDetailDao")
  private BranchDetailDao branchDetailDao;

  @Override
  public BranchDetailModel getBranchDetailService(String branchId) {
    // TODO Auto-generated method stub
    BranchDetailModel branchdetails = branchDetailDao.getBranchDetails(branchId);
    return branchdetails;
  }

  @Override
  public int deleteBranchDetails(String branchId) {
    // TODO Auto-generated method stub
    return branchDetailDao.deleteBranchdao(branchId);
  }
  
  @Override
  public int upadateBranchDetails(BranchDetailModel branchId) {
    // TODO Auto-generated method stub
    return branchDetailDao.updateBranchdao(branchId);
  }

  @Override
  public int createBranch(BranchDetailModel branch) {
    // TODO Auto-generated method stub
    int branchdetails =branchDetailDao.createBranchdao(branch);
    return branchdetails;
  }
}
  