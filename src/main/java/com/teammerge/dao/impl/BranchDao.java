package com.teammerge.dao.impl;

import java.util.List;

import com.teammerge.dao.BaseDao;
import com.teammerge.model.BranchDetailModel;

public interface BranchDao extends BaseDao<BranchDetailModel> {
  public List<BranchDetailModel> fetchEntityLike(String entityId);
}
