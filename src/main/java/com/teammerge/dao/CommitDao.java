package com.teammerge.dao;


import java.util.List;

import com.teammerge.entity.CommitModel;

  public interface CommitDao extends BaseDao<CommitModel> {
    public List<CommitModel> fetchEntityLike(String entityId);

    public int deleteEntityForProject(String projectName);
}