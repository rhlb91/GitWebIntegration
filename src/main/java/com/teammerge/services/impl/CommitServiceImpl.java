package com.teammerge.services.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.jgit.errors.AmbiguousObjectException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.teammerge.Constants;
import com.teammerge.cache.CommitCache;
import com.teammerge.dao.BaseDao;
import com.teammerge.dao.CommitDao;
import com.teammerge.entity.CommitModel;
import com.teammerge.form.CommitForm;
import com.teammerge.model.CustomRefModel;
import com.teammerge.model.RefModel;
import com.teammerge.model.RepositoryCommit;
import com.teammerge.services.CommitService;
import com.teammerge.services.RepositoryService;
import com.teammerge.strategy.CommitDiffStrategy;
import com.teammerge.utils.HibernateUtils;
import com.teammerge.utils.StringUtils;
import com.teammerge.utils.TimeUtils;

@Service("commitService")
public class CommitServiceImpl implements CommitService {
  private static final Logger LOG = LoggerFactory.getLogger(CommitServiceImpl.class);

  private BaseDao<CommitModel> baseDao;

  private CommitDao commitDao;

  @Resource(name = "repositoryService")
  RepositoryService repositoryService;

  @Resource(name = "commitDiffStrategy")
  private CommitDiffStrategy commitDiffStrategy;

  @Value("${git.commit.timeFormat}")
  private String commitTimeFormat;

  @Value("${app.dateFormat}")
  private String commitDateFormat;

  @Value("${app.debug}")
  private String debug;

  public boolean isDebugOn() {
    return Boolean.parseBoolean(debug);
  }

  /**
   * Here a branch represents a ticket, commits inside a branch represents the work done for that
   * ticket <br>
   * 
   * Thus branch name should be same as ticket id
   */
  public Map<String, List<CommitModel>> getDetailsForBranchName(String branchName) {

    Map<String, List<CommitModel>> commitsPerMatchedBranch = new HashMap<>();
    List<RepositoryCommit> repoCommits = new ArrayList<>();
    List<CommitModel> commits = new ArrayList<CommitModel>();
    int numOfMatchedBranches = 0;
    Date minimumDate = TimeUtils.getInceptionDate();

    List<CustomRefModel> customRefModels = repositoryService.getCustomRefModels(false);

    if (CollectionUtils.isNotEmpty(customRefModels)) {
      for (CustomRefModel branch : customRefModels) {
        repoCommits.clear();
        if (branch.getRefModel().getName().contains(branchName)) {
          ++numOfMatchedBranches;

          if (branch.getRepository() != null) {
            List<RepositoryCommit> commitsPerBranch =
                CommitCache.instance().getCommits(branch.getRepositoryName(),
                    branch.getRepository(), branch.getRefModel().getName(), minimumDate);

            commits.addAll(populateCommits(commitsPerBranch, branch.getRepositoryName(),
                branch.getRefModel()));
          }
          commitsPerMatchedBranch.put(branch.getRefModel().getName(), commits);
        }
      }
    }

    if (isDebugOn()) {
      LOG.debug("Num of branches: " + numOfMatchedBranches + ", Num of commits: " + commits.size());
    }

    return commitsPerMatchedBranch;
  }

  public List<CommitModel> populateCommits(List<RepositoryCommit> commits, String repoName,
      RefModel branch) {
    List<CommitModel> populatedCommits = new ArrayList<CommitModel>();

    for (RepositoryCommit commit : commits) {
      CommitModel commitModel = new CommitModel();
      commitModel.setCommitAuthor(commit.getAuthorIdent());
      // short message
      String shortMessage = commit.getShortMessage();
      String trimmedMessage = shortMessage;
      if (commit.getRefs() != null && commit.getRefs().size() > 0) {
        trimmedMessage = StringUtils.trimString(shortMessage, Constants.LEN_SHORTLOG_REFS);
      } else {
        trimmedMessage = StringUtils.trimString(shortMessage, Constants.LEN_SHORTLOG);
      }
      commitModel.setShortMessage(shortMessage);
      commitModel.setTrimmedMessage(trimmedMessage);

      // commit hash link
      int hashLen = 6;
      if (commit.getName() != null) {
        commitModel.setCommitHash(commit.getName().substring(0, hashLen));
      }
      commitModel.setCommitId(commit.getName());
      if (commitModel.getShortMessage().startsWith("Merge")) {
        commitModel.setIsMergeCommit(true);
      } else {
        commitModel.setIsMergeCommit(false);
      }

      commitModel.setCommitDate(commit.getCommitDate());
      commitModel.setCommitTimeFormatted(TimeUtils.convertToDateFormat(commit.getCommitDate(),
          commitTimeFormat));
      commitModel.setBranchName(branch.displayName);
      commitModel.setRepositoryName(repoName);

      setParent(commitModel, commit.getParents());
      commitModel.setParentCount(commit.getParentCount());

      populatedCommits.add(commitModel);
    }
    return populatedCommits;
  }

  private void setParent(CommitModel commitModel, RevCommit[] parentCommits) {
    List<String> parents = new ArrayList<>();

    if (parents != null) {
      for (RevCommit commit : parentCommits) {
        parents.add(commit.getName());
      }
      commitModel.setParents(parents);
    }
  }

  @Override
  public List<CommitModel> getCommitDetails(String branchName) {
    List<CommitModel> commitModel = commitDao.fetchEntityLike(branchName);
    return commitModel;
  }

  @Override
  public void saveCommit(CommitModel commit) {
    getBaseDao().saveEntity(commit);
  }

  @Override
  public void saveOrUpdateCommit(CommitModel commit) {
    getBaseDao().saveOrUpdateEntity(commit);
  }

  @Override
  public void saveOrUpdateCommitInSeparateSession(CommitModel commit) {
    Session session = HibernateUtils.getSessionFactory().openSession();
    Transaction transaction = null;
    try {
      transaction = session.beginTransaction();
      getBaseDao().saveInSeparateSession(session, commit);
      transaction.commit();
    } catch (HibernateException e) {
      transaction.rollback();
      LOG.error("Commit " + commit.getCommitId() + " from branch " + commit.getBranchName()
          + " not saved!!");
    } finally {
      session.close();
    }
  }


  public BaseDao<CommitModel> getBaseDao() {
    return baseDao;
  }

  @Autowired
  public void setBaseDao(BaseDao<CommitModel> baseDao) {
    baseDao.setClazz(CommitModel.class);
    this.baseDao = baseDao;
  }

  /**
   * This method is used to save/update commitdetails in Dao, Using CommitForm to populate the data
   * for binding in Json format at the times parsing data from CommitModel to databases
   */
  @Override
  public void saveOrUpdateCommitDetails(CommitForm commitForm) {
    CommitModel model = new CommitModel();

    model.setCommitId(commitForm.getCommitId());
    model.setCommitAuthor(new PersonIdent(commitForm.getAuthorName(), commitForm.getAuthorEmail(),
        Long.valueOf(commitForm.getWhen()), Integer.valueOf(commitForm.getTimezone())));
    model.setBranchName(commitForm.getBranchName());
    model.setCommitDate(TimeUtils.convertToDateFormat(Long.valueOf(commitForm.getCommitDate())));
    model.setCommitHash(commitForm.getCommitHash());
    model.setCommitTimeFormatted(commitForm.getFormattedTime());
    model.setIsMergeCommit(Boolean.valueOf(commitForm.getIsMergeCommit()));
    model.setRepositoryName(commitForm.getRepoName());
    model.setShortMessage(commitForm.getShortMsg());
    model.setTrimmedMessage(commitForm.getTrimmedMsg());

    getBaseDao().saveOrUpdateEntity(model);

  }

  public List<String> getCommitDiff(String repoName, String branch, String commitId)
      throws RevisionSyntaxException, AmbiguousObjectException, IncorrectObjectTypeException,
      IOException {
    Repository r = repositoryService.getRepository(repoName);
    return commitDiffStrategy.getCommitDif(r, null, commitId);
  }

  @Override
  public List<CommitModel> getCommitDetailsAll() {
    return getBaseDao().fetchAll();
  }

  @Autowired
  public void setCommitDao(CommitDao commitDao) {
    commitDao.setClazz(CommitModel.class);
    this.commitDao = commitDao;
  }

}
