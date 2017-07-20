package com.teammerge.services.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teammerge.dao.BaseDao;
import com.teammerge.model.BranchDetailModel;
import com.teammerge.services.BranchDetailService;

@Service("branchDetailService")
public class BranchDetailServiceImpl implements BranchDetailService {

  private BaseDao<BranchDetailModel> baseDao;

  @Override
  public BranchDetailModel getBranchDetails(String branchId) {
    BranchDetailModel branchdetails = getBaseDao().fetchEntity(branchId);
    return branchdetails;
  }

  @Override
  public int saveBranch(BranchDetailModel branch) {
    getBaseDao().saveEntity(branch);
    return 0;
  }

  public BaseDao<BranchDetailModel> getBaseDao() {
    return baseDao;
  }

  @Autowired
  public void setBaseDao(BaseDao<BranchDetailModel> baseDao) {
    baseDao.setClazz(BranchDetailModel.class);
    this.baseDao = baseDao;
  }
}
