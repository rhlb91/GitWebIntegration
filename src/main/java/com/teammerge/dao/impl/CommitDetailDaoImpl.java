package com.teammerge.dao.impl;

import java.util.List;

import org.hibernate.Query;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import com.teammerge.dao.CommitDetailDao;
import com.teammerge.model.CommitModel;
import com.teammerge.utils.HibernateUtils;

@Repository("commitDao")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CommitDetailDaoImpl extends BaseDaoImpl<CommitModel> implements CommitDetailDao {

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

  }
