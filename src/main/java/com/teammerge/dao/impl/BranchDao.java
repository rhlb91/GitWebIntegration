package com.teammerge.dao.impl;

import java.util.List;

import com.teammerge.dao.BaseDao;
import com.teammerge.model.BranchModel;

public interface BranchDao extends BaseDao<BranchModel> {
  public List<BranchModel> fetchEntityLike(String entityId);
}
