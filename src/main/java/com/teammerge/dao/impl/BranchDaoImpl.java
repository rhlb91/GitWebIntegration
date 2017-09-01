package com.teammerge.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.teammerge.dao.BranchDao;
import com.teammerge.entity.BranchModel;
import com.teammerge.utils.HibernateUtils;

@Repository("branchDao")
public class BranchDaoImpl extends BaseDaoImpl<BranchModel> implements BranchDao {

  @SuppressWarnings("unchecked")
  public List<BranchModel> fetchEntityLike(String entityId) {
    final String queryStr = "From BranchModel as b where b.branchId like :branchId";

    HibernateUtils.openCurrentSession();
    Query qry = HibernateUtils.getCurrentSession().createQuery(queryStr);
    qry.setString("branchId", "%" + entityId + "%");
    List<BranchModel> result = qry.list();

    HibernateUtils.closeCurrentSession();
    return result;
  }

}
