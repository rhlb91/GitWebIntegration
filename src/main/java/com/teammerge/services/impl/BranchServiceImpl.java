package com.teammerge.services.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.teammerge.dao.BaseDao;
import com.teammerge.dao.BranchDao;
import com.teammerge.entity.BranchLastCommitAdded;
import com.teammerge.entity.BranchModel;
import com.teammerge.form.BranchForm;
import com.teammerge.model.CustomRefModel;
import com.teammerge.populator.BranchPopulator;
import com.teammerge.services.BranchService;
import com.teammerge.services.RepositoryService;
import com.teammerge.utils.HibernateUtils;
import com.teammerge.utils.StringUtils;

@Service("branchService")
public class BranchServiceImpl implements BranchService {

  private final Logger LOG = LoggerFactory.getLogger(getClass());

  @Resource(name = "repositoryService")
  RepositoryService repositoryService;

  private BranchDao branchDao;

  private BaseDao<BranchLastCommitAdded> baseDao;

  @Resource(name = "branchPopulator")
  private BranchPopulator branchPopulator;

  @Override
  public List<BranchModel> getBranchesWithMinimumDetails(String branchName) {
    List<BranchModel> branches = new ArrayList<>();
    List<CustomRefModel> branchModels = repositoryService.getCustomRefModels(false);

    if (CollectionUtils.isNotEmpty(branchModels)) {
      for (CustomRefModel branch : branchModels) {
        String fullBranchName = branch.getRefModel().getName();
        if (!StringUtils.isEmpty(fullBranchName) && fullBranchName.contains(branchName)) {
          branches.add(populateBranch(fullBranchName, branchName));
        }
      }
    }
    return branches;
  }

  public BranchModel populateBranch(String fullBranchName, String name) {
    BranchModel branchModel = new BranchModel();
    branchModel.setBranchId(fullBranchName);
    branchModel.setShortName(name);

    return branchModel;
  }

  @Override
  public BranchModel getBranchDetails(String branchId) {
    BranchModel branchdetails = branchDao.fetchEntity(branchId);
    return branchdetails;
  }

  @Override
  public void saveBranch(BranchForm branchForm) {
    BranchModel branchModel = new BranchModel();
    branchPopulator.populate(branchForm, branchModel);
    branchDao.saveOrUpdateEntity(branchModel);
  }

  @Override
  public int saveBranch(BranchModel branch) {
    branchDao.saveEntity(branch);
    return 0;
  }


  @Override
  public int saveOrUpdateBranch(BranchModel branch) {
    branchDao.saveOrUpdateEntity(branch);
    return 0;
  }

  @Override
  public void saveOrUpdateBranchInSeparateSession(BranchModel branch) {
    Session session = HibernateUtils.getSessionFactory().openSession();
    Transaction transaction = null;
    try {
      transaction = session.beginTransaction();
      branchDao.saveInSeparateSession(session, branch);
      transaction.commit();
    } catch (HibernateException e) {
      transaction.rollback();
      LOG.error("Branch " + branch.getBranchId() + " from branch " + branch.getRepositoryId()
          + " not saved!!");
    } finally {
      session.close();
    }
  }

  @Override
  public List<BranchModel> getBranchDetailsForBranchLike(String branchId) {
    List<BranchModel> branchdetails = branchDao.fetchEntityLike(branchId);
    List<BranchModel> validBranches = new ArrayList<>();

    String[] strArr = {branchId + "_", branchId + "-", branchId + " "};
    boolean isValidTicket = false;

    for (BranchModel b : branchdetails) {
      String bName = b.getShortName().substring(b.getShortName().lastIndexOf("/") + 1);
      isValidTicket = false;
      for (int i = 0; i < strArr.length; i++) {
        if (bName.equals(branchId) || bName.contains(strArr[i])) {
          isValidTicket = true;
          break;
        }
      }
      if (isValidTicket) {
        validBranches.add(b);
      }
    }
    return validBranches;
  }

  @Override
  public Date getLastCommitDateAddedInBranch(String entityKey) {
    Date lastCommitDate = null;
    BranchLastCommitAdded entity = baseDao.fetchEntity(entityKey);
    if (entity != null) {
      lastCommitDate = entity.getLastModified();
    }
    return lastCommitDate;
  }

  public void updateLastCommitDateAddedInBranch(String entityKey, Date date) {
    BranchLastCommitAdded model = new BranchLastCommitAdded(entityKey, date);
    baseDao.saveOrUpdateEntity(model);
  }

  @Autowired
  public void setBranchDao(BranchDao branchDao) {
    branchDao.setClazz(BranchModel.class);
    this.branchDao = branchDao;
  }

  @Autowired
  public void setBranchLastCommitAddedDao(BaseDao<BranchLastCommitAdded> baseDao) {
    baseDao.setClazz(BranchLastCommitAdded.class);
    this.baseDao = baseDao;
  }

  public BaseDao<BranchLastCommitAdded> getBranchLastCommitAddedDao() {
    return baseDao;
  }

}
