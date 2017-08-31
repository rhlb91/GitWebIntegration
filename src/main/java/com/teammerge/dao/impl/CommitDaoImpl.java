package com.teammerge.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import com.teammerge.dao.CommitDao;
import com.teammerge.entity.CommitModel;
import com.teammerge.utils.HibernateUtils;

@Repository("commitDao")
public class CommitDaoImpl extends BaseDaoImpl<CommitModel> implements CommitDao {

  @SuppressWarnings("unchecked")
  public List<CommitModel> fetchEntityLike(String branchName) {
    final String queryStr = "From CommitModel as c where c.branchName like :branchName";

    HibernateUtils.openCurrentSession();
    Query qry = HibernateUtils.getCurrentSession().createQuery(queryStr);
    qry.setString("branchName", "%" + branchName + "%");
    List<CommitModel> result = qry.list();

    HibernateUtils.closeCurrentSession();
    return result;
  }

  @Override
  public int deleteEntityForProject(String projectName) {
    final String queryStr = "delete from  CommitModel as c where c.repositoryName = ?projectId";

    HibernateUtils.openCurrentSessionwithTransaction();
    Query qry = HibernateUtils.getCurrentSession().createQuery(queryStr);
    qry.setParameter("projectId", projectName);
    int result = qry.executeUpdate();
    HibernateUtils.closeCurrentSessionwithTransaction();

    return result;
  }

}
