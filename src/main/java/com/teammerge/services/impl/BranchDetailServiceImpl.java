package com.teammerge.services.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teammerge.dao.impl.BranchDao;
import com.teammerge.model.BranchDetailModel;
import com.teammerge.services.BranchDetailService;

@Service("branchDetailService")
public class BranchDetailServiceImpl implements BranchDetailService {

  private BranchDao branchDao;

  @Override
  public BranchDetailModel getBranchDetails(String branchId) {
    BranchDetailModel branchdetails = branchDao.fetchEntity(branchId);
    return branchdetails;
  }

  @Override
  public int saveBranch(BranchDetailModel branch) {
    branchDao.saveEntity(branch);
    return 0;
  }

  @Override
  public List<BranchDetailModel> getBranchDetailsForBranchLike(String branchId) {
    List<BranchDetailModel> branchdetails = branchDao.fetchEntityLike(branchId);
    return branchdetails;
  }

  @Autowired
  public void setBaseDao(BranchDao branchDao) {
    branchDao.setClazz(BranchDetailModel.class);
    this.branchDao = branchDao;
  }
}
