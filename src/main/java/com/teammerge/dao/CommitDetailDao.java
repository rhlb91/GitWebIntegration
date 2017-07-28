package com.teammerge.dao;


import java.util.List;

import com.teammerge.model.CommitModel;

  public interface CommitDetailDao extends BaseDao<CommitModel> {
    public List<CommitModel> fetchEntityLike(String entityId);
}