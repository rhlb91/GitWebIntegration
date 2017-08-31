package com.teammerge.dao;

import java.util.List;

import com.teammerge.entity.BranchModel;

public interface BranchDao extends BaseDao<BranchModel> {
  public List<BranchModel> fetchEntityLike(String entityId);
  
  int deleteEntityForProject(final String projectId);
}
