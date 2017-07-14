package com.teammerge.dao.impl;

import org.springframework.stereotype.Repository;

import com.teammerge.dao.BranchDetailDao;
import com.teammerge.model.BranchDetailModel;
import com.teammerge.utils.HibernateUtils;

@Repository("branchDetailDao")
public class BranchDetailDaoImpl implements BranchDetailDao {


  public BranchDetailModel getBranchDetails(String branchId) {
    BranchDetailModel branchDetails =
        (BranchDetailModel) HibernateUtils.openCurrentSession().get(BranchDetailModel.class,
            branchId);
    return branchDetails;
  }

  public void createBranch(BranchDetailModel branch) {
    HibernateUtils.openCurrentSessionwithTransaction();
    HibernateUtils.getCurrentSession().save(branch);
    HibernateUtils.closeCurrentSessionwithTransaction();
  }

}
