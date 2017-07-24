package com.teammerge.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.teammerge.model.BranchDetailModel;
import com.teammerge.utils.HibernateUtils;

@Repository("branchDao")
public class BranchDaoImpl extends BaseDaoImpl<BranchDetailModel> implements BranchDao {

  @SuppressWarnings("unchecked")
  public List<BranchDetailModel> fetchEntityLike(String entityId) {
    final String queryStr = "From BranchDetailModel as b where b.branchId like :branchId";

    HibernateUtils.openCurrentSession();
    Query qry = HibernateUtils.getCurrentSession().createQuery(queryStr);
    qry.setString("branchId", "%" + entityId + "%");
    List<BranchDetailModel> result = qry.list();

    HibernateUtils.closeCurrentSession();
    return result;
  }
}
